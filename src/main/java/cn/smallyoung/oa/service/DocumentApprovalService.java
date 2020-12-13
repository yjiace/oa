package cn.smallyoung.oa.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.DocumentApprovalDao;
import cn.smallyoung.oa.entity.AttachmentFile;
import cn.smallyoung.oa.entity.DocumentApproval;
import cn.smallyoung.oa.entity.DocumentApprovalNode;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.vo.DocumentApprovalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @data 2020/11/20
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class DocumentApprovalService extends BaseService<DocumentApproval, Long> {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private DocumentApprovalDao documentApprovalDao;
    @Resource
    private AttachmentFileService attachmentFileService;
    @Resource
    private MessageNotificationService messageNotificationService;

    @Override
    public DocumentApproval findOne(Long id) {
        return super.findOne(id);
    }

    /**
     * 查询需要我审批的审批
     *
     * @param page  页码
     * @param limit 页数
     */
    public Page<DocumentApproval> findAllApprovalRequired(Integer page, Integer limit) {
        String username = sysUserService.currentlyLoggedInUser();
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "sort"));
        long count = documentApprovalDao.countApprovalRequired(username);
        if (count <= 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<DocumentApproval> approvals = documentApprovalDao.findAllApprovalRequired(username, limit, (page - 1) * limit);
        return new PageImpl<>(approvals, pageable, count);
    }

    /**
     * 撤回审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval withdrawalOfApproval(DocumentApproval approval) {
        List<DocumentApprovalNode> nodes = approval.getDocumentApprovalNodes();
        if (CollUtil.isNotEmpty(nodes)) {
            nodes.forEach(n -> n.setStatus("Withdrawn"));
        }
        approval.setStatus("Withdrawn");
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "withdrawalOfApproval", "已经成功撤销您提交的审批");
        documentApprovalDao.save(approval);
        return approval;
    }

    /**
     * 提交审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval submitForApproval(DocumentApprovalVO documentApprovalVO) {
        DocumentApproval approval = new DocumentApproval();
        BeanUtil.copyProperties(documentApprovalVO, approval, CopyOptions.create().setIgnoreNullValue(true));
        List<String> usernameList = documentApprovalVO.getUsername().stream().distinct().collect(Collectors.toList());
        //查询用户集合
        List<SysUser> userList = sysUserService.findByUsernameIn(usernameList);
        if (usernameList.size() != userList.size()) {
            List<String> checkUser = userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
            String error = String.format("用户【%s】为无效用户", usernameList.stream()
                    .filter(s -> !checkUser.contains(s)).collect(Collectors.joining(",")));
            log.error(error);
            throw new RuntimeException(error);
        }
        approval.setInitiatorUsername(sysUserService.currentlyLoggedInUser());
        approval.setStatus("Approval");

        List<AttachmentFile> attachmentFiles = attachmentFileService.uploadFile(documentApprovalVO.getFile(), documentApprovalVO.getSecurityClassification());
        //上传的文件列表
        approval.setAttachmentFiles(attachmentFiles);
        documentApprovalDao.save(approval);
        List<DocumentApprovalNode> nodes = new ArrayList<>();
        DocumentApprovalNode approvalNode;
        for (int i = 0; i < usernameList.size(); i++) {
            approvalNode = new DocumentApprovalNode();
            approvalNode.setStatus(i == 0 ? "Approval" : "NotStarted");
            approvalNode.setUser(usernameList.get(i));
            approvalNode.setSort(i);
            approvalNode.setDocumentApproval(approval);
            nodes.add(approvalNode);
        }
        approval.setNode(nodes.get(0));
        approval.setDocumentApprovalNodes(nodes);
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "submitForApproval", "您提交的审批已进入审批流程");
        messageNotificationService.releaseMessage(usernameList.get(0), "submitForApproval", "有需要您的审批，请查看");
        documentApprovalDao.save(approval);
        return approval;
    }

    /**
     * 同意审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval completedApproval(DocumentApproval approval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<DocumentApprovalNode> nodes = approval.getDocumentApprovalNodes();
        DocumentApprovalNode node;
        String userName;
        SysUser user;
        boolean needNextUserApproval = false;
        for (int i = 0, size = nodes.size(); i < size; i++) {
            node = nodes.get(i);
            if (needNextUserApproval) {
                //校验审批用户
                userName = node.getUser();
                user = sysUserService.findOne(userName);
                if (user != null && "Y".equals(user.getStatus()) && "N".equals(user.getIsDelete())) {
                    approval.setNode(node);
                    node.setStatus("Approval");
                    messageNotificationService.releaseMessage(user.getUsername(), "completedApproval", "有需要您的审批，请查看");
                    break;
                } else if (i == (size - 1)) {
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
                } else {
                    needNextUserApproval = true;
                    messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "completedApproval",
                            String.format("【%s】已经同意您提交的审批", username));
                }
                messageNotificationService.releaseMessage(node.getUser(), "rejectedApproval",
                        String.format("您同意了用户【%s】提交的审批", approval.getInitiatorUsername()));
            }
        }
        documentApprovalDao.save(approval);
        return approval;
    }

    /**
     * 重新审批（被拒绝）
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval reApprove(DocumentApproval approval) {
        approval.setStatus("Approval");
        approval.getDocumentApprovalNodes().forEach(node -> {
            if (approval.getNode().getUser().equals(node.getUser())) {
                node.setStatus("Approval");
                messageNotificationService.releaseMessage(node.getUser(), "reApprove", "有需要您的审批，请查看");
            }
        });
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "reApprove", "您提交的审批已重新进入审批流程");
        documentApprovalDao.save(approval);
        return approval;
    }

    /**
     * 拒绝审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval rejectedApproval(DocumentApproval approval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<DocumentApprovalNode> nodes = approval.getDocumentApprovalNodes();
        DocumentApprovalNode node;
        for (DocumentApprovalNode approvalNode : nodes) {
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
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "rejectedApproval", "您提交的审批未通过审核。");
        documentApprovalDao.save(approval);
        return approval;
    }

    /**
     * 检查用户是否有正在进行中的审批
     */
    public void checkUserHaveApproval(SysUser user) {
        if (documentApprovalDao.checkUserHaveApproval(user.getUsername()) > 0) {
            String error = String.format("当前用户[%s]有正在进行中的审批，请先结束审批", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        } else if (documentApprovalDao.countApprovalRequired(user.getUsername()) > 0) {
            String error = String.format("当前用户[%s]有需要审批的文件，请先审批完成", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }

    }
}