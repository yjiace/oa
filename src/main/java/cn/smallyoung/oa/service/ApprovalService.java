package cn.smallyoung.oa.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.ApprovalDao;
import cn.smallyoung.oa.entity.*;
import cn.smallyoung.oa.vo.ApprovalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @data 2020/11/20
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ApprovalService extends BaseService<Approval, Long> {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private ApprovalDao approvalDao;
    @Resource
    private AttachmentFileService attachmentFileService;
    @Resource
    private VehicleInformationService vehicleInformationService;
    @Resource
    private MessageNotificationService messageNotificationService;

    public Map<String, Consumer<Approval>> checkApproval = new HashMap<>();

    public boolean checkNumber(String number){
        return StrUtil.isNotBlank(number) && approvalDao.findByNumber(number) != null;
    }

    @PostConstruct
    private void init() {
        checkApproval.put("vehicle", vehicleInformationService::checkApproval);
    }

    @Override
    public Approval findOne(Long id){
        return super.findOne(id);
    }

    /**
     * 查询需要我审批的审批
     *
     * @param page  页码
     * @param limit 页数
     */
    public Page<Approval> findAllApprovalRequired(Integer page, Integer limit, String type) {
        String username = sysUserService.currentlyLoggedInUser();
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "sort"));
        long count = approvalDao.countApprovalRequired(username, type);
        if (count <= 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<Approval> approvals = approvalDao.findAllApprovalRequired(username, type, limit, (page - 1) * limit);
        return new PageImpl<>(approvals, pageable, count);
    }

    /**
     * 撤回审批
     */
    @Transactional(rollbackFor = Exception.class)
    public Approval withdrawalOfApproval(Approval approval) {
        List<ApprovalNode> nodes = approval.getApprovalNodes();
        if (CollUtil.isNotEmpty(nodes)) {
            nodes.forEach(n -> n.setStatus("Withdrawn"));
        }
        approval.setStatus("Withdrawn");
        approval.getApprovalLogs().add(approvalLog(approval, approval.getId(),
                ApprovalLogOperation.withdrawal, ApprovalLogOperationType.approval, null));
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "withdrawalOfApproval", "已经成功撤销您提交的审批");
        approvalDao.save(approval);
        vehicleInformationService.withdrawalOfApproval(approval);
        return approval;
    }

    /**
     * 提交审批
     */
    @Transactional(rollbackFor = Exception.class)
    public Approval submitForApproval(ApprovalVO approvalVO) {
        Approval approval = new Approval();
        BeanUtil.copyProperties(approvalVO, approval, CopyOptions.create().setIgnoreNullValue(true));
        Consumer<Approval> consumer = checkApproval.get(approvalVO.getType());
        if(consumer != null){
            consumer.accept(approval);
        }
        List<String> usernameList = approvalVO.getUsername().stream().distinct().collect(Collectors.toList());
        //查询用户集合
        List<SysUser> userList = sysUserService.findByUsernameIn(usernameList);
        if(usernameList.size() != userList.size()){
            List<String> checkUser = userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
            String error = String.format("用户【%s】为无效用户", usernameList.stream()
                    .filter(s -> !checkUser.contains(s)).collect(Collectors.joining(",")));
            log.error(error);
            throw new RuntimeException(error);
        }
        approval.setSort(0);
        approval.setInitiatorUsername(sysUserService.currentlyLoggedInUser());
        approval.setStatus("Approval");

        List<AttachmentFile> attachmentFiles = attachmentFileService.uploadFile(approvalVO.getFile(),
                approvalVO.getDocumentNumber(), approvalVO.getSecurityClassification());
        if (CollUtil.isNotEmpty(attachmentFiles)) {
            attachmentFiles.forEach(file -> approval.getApprovalLogs().add(approvalLog(approval, file.getId(),
                    ApprovalLogOperation.add, ApprovalLogOperationType.attachmentFile, null)));
        }
        //上传的文件列表
        approval.setAttachmentFiles(attachmentFiles);
        approvalDao.save(approval);
        List<ApprovalNode> nodes = new ArrayList<>();
        ApprovalNode approvalNode;
        for (int i = 0; i < usernameList.size(); i++) {
            approvalNode = new ApprovalNode();
            approvalNode.setStatus(i == 0 ? "Approval" : "NotStarted");
            approvalNode.setUser(usernameList.get(i));
            approvalNode.setSort(i);
            approvalNode.setApproval(approval);
            nodes.add(approvalNode);
        }
        approval.setNode(nodes.get(0));
        approval.setApprovalNodes(nodes);
        approval.getApprovalLogs().add(approvalLog(approval, approval.getId(),
                ApprovalLogOperation.add, ApprovalLogOperationType.approval, null));
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "submitForApproval", "您提交的审批已进入审批流程");
        approvalDao.save(approval);
        vehicleInformationService.submitApproval(approval);
        return approval;
    }

    /**
     * 同意审批
     */
    @Transactional(rollbackFor = Exception.class)
    public Approval completedApproval(Approval approval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<ApprovalNode> nodes = approval.getApprovalNodes();
        ApprovalNode node;
        String userName;
        SysUser user;
        boolean needNextUserApproval = false;
        boolean completed = false;
        for (int i = 0, size = nodes.size(); i < size; i++) {
            node = nodes.get(i);
            if (needNextUserApproval) {
                //校验审批用户
                userName = node.getUser();
                user = sysUserService.findOne(userName);
                if(user != null && "Y".equals(user.getStatus()) && "N".equals(user.getIsDelete())){
                    approval.setNode(node);
                    node.setStatus("Approval");
                    break;
                }else if(i == (size - 1)){
                    approval.setStatus("Completed");
                    messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "completedApproval",
                            "您提交的审批已经审核通过");
                }
                node.setStatus("NotStarted");
            } else if (node.getUser().equals(username)) {
                node.setStatus("Completed");
                node.setCompletedTime(LocalDateTime.now());
                //最后的审批者
                if (i == (size - 1)) {
                    approval.setStatus("Completed");
                    messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "completedApproval",
                            "您提交的审批已经审核通过");
                    completed = true;
                } else {
                    needNextUserApproval = true;
                    messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "completedApproval",
                             String.format("【%s】已经同意您提交的审批", username));
                }
                messageNotificationService.releaseMessage(node.getUser(), "rejectedApproval",
                        String.format("您同意了用户【%s】提交的审批", approval.getInitiatorUsername()));
            }
        }
        approval.getApprovalLogs().add(approvalLog(approval, approval.getId(),
                ApprovalLogOperation.agree, ApprovalLogOperationType.approval, null));
        approvalDao.save(approval);
        if(completed){
            vehicleInformationService.completedApproval(approval);
        }
        return approval;
    }

    /**
     * 重新审批（被拒绝）
     */
    @Transactional(rollbackFor = Exception.class)
    public Approval reApprove(Approval approval) {
        Consumer<Approval> consumer = checkApproval.get(approval.getType());
        if(consumer != null){
            consumer.accept(approval);
        }
        approval.setStatus("Approval");
        approval.getApprovalNodes().forEach(node -> {
            if (approval.getNode().getUser().equals(node.getUser())) {
                node.setStatus("Approval");
            }
        });
        approval.getApprovalLogs().add(approvalLog(approval, approval.getId(),
                ApprovalLogOperation.reApprove, ApprovalLogOperationType.approval, null));
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "reApprove", "您提交的审批已重新进入审批流程");
        approvalDao.save(approval);
        vehicleInformationService.submitApproval(approval);
        return approval;
    }

    /**
     * 拒绝审批
     */
    @Transactional(rollbackFor = Exception.class)
    public Approval rejectedApproval(Approval approval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<ApprovalNode> nodes = approval.getApprovalNodes();
        ApprovalNode node;
        for (ApprovalNode approvalNode : nodes) {
            node = approvalNode;
            if (node.getUser().equals(username)) {
                node.setStatus("Rejected");
                approval.setNode(node);
                node.setCompletedTime(LocalDateTime.now());
                approval.setStatus("Rejected");
                messageNotificationService.releaseMessage(node.getUser(), "rejectedApproval",
                        String.format("您已经拒绝用户【%s】提交的审批", approval.getInitiatorUsername()));
                break;
            }
        }
        approval.getApprovalLogs().add(approvalLog(approval, approval.getId(),
                ApprovalLogOperation.rejected, ApprovalLogOperationType.approval, null));
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "rejectedApproval", "您提交的审批未通过审核。");
        approvalDao.save(approval);
        vehicleInformationService.rejectedApproval(approval);
        return approval;
    }

    /**
     * 检查用户是否有正在进行中的审批
     */
    public void checkUserHaveApproval(SysUser user){
        if(approvalDao.checkUserHaveApproval(user.getUsername()) > 0){
            String error = String.format("当前用户[%s]有正在进行中的审批，请先结束审批", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }else if(approvalDao.countApprovalRequired(user.getUsername()) > 0){
            String error = String.format("当前用户[%s]有需要审批的文件，请先审批完成", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }

    }

    public ApprovalLog approvalLog(Approval approval, Long operationId, ApprovalLogOperation operation,
                                           ApprovalLogOperationType operationType, String message) {
        ApprovalLog log = new ApprovalLog();
        log.setUsername(sysUserService.currentlyLoggedInUser());
        log.setOperation(operation.name());
        log.setOperationId(operationId);
        log.setApproval(approval);
        log.setOperationType(operationType.name());
        log.setOperationMessage(message);
        return log;
    }

    /**
     * 添加评论
     */
    @Transactional(rollbackFor = Exception.class)
    public Approval addComment(Approval approval, String message) {
        ApprovalComment comment = new ApprovalComment();
        comment.setApproval(approval);
        comment.setIsDelete("N");
        comment.setMessage(message);
        approval.getApprovalComments().add(comment);
        approval.getApprovalLogs().add(approvalLog(approval, approval.getId(),
                ApprovalLogOperation.add, ApprovalLogOperationType.approvalComments, message));
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "addComment", "您提交的审批有了新的评论。");
        return approvalDao.save(approval);
    }

}

enum ApprovalLogOperation {
    /**
     * 增加
     */
    add,
    /**
     * 重新审批
     */
    reApprove,
    /**
     * 拒绝审批
     */
    rejected,
    /**
     * 撤回审批
     */
    withdrawal,
    /**
     * 同意审批
     */
    agree
}

enum ApprovalLogOperationType {
    /**
     * 附件
     */
    attachmentFile,
    /**
     * 审批
     */
    approval,
    /**
     * 评论
     */
    approvalComments
}


