package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.DocumentApprovalDao;
import cn.smallyoung.oa.entity.*;
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

    /**
     * 查询需要我审批的文件审批
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
        List<DocumentApproval> documentApprovals = documentApprovalDao.findAllApprovalRequired(username, limit, (page - 1) * limit);
        return new PageImpl<>(documentApprovals, pageable, count);
    }

    /**
     * 撤回审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval withdrawalOfApproval(DocumentApproval documentApproval) {
        List<DocumentApprovalNode> nodes = documentApproval.getDocumentApprovalNodes();
        if (CollUtil.isNotEmpty(nodes)) {
            nodes.forEach(n -> n.setStatus("Withdrawn"));
        }
        documentApproval.setStatus("Withdrawn");
        documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, documentApproval.getId(),
                DocumentApprovalLogOperation.withdrawal, DocumentApprovalLogOperationType.documentApproval, null));
        messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "withdrawalOfApproval",
                "已经成功撤销您提交的文件审批");
        return documentApprovalDao.save(documentApproval);
    }

    /**
     * 提交审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval submitForApproval(DocumentApprovalVO documentApprovalVO) {
        List<String> usernameList = documentApprovalVO.getUsername().stream().distinct().collect(Collectors.toList());
        //查询用户集合
        List<SysUser> userList = sysUserService.findByUsernameIn(usernameList);
        if(usernameList.size() != userList.size()){
            List<String> checkUser = userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
            String error = String.format("用户【%s】为无效用户", usernameList.stream()
                    .filter(s -> !checkUser.contains(s)).collect(Collectors.joining(",")));
            log.error(error);
            throw new RuntimeException(error);
        }
        DocumentApproval documentApproval = new DocumentApproval();
        documentApproval.setSort(0);
        documentApproval.setInitiatorUsername(sysUserService.currentlyLoggedInUser());
        documentApproval.setStatus("Approval");

        List<AttachmentFile> attachmentFiles = attachmentFileService.uploadFile(documentApprovalVO.getFile(),
                documentApprovalVO.getDocumentNumber(), documentApprovalVO.getSecurityClassification());
        if (CollUtil.isNotEmpty(attachmentFiles)) {
            attachmentFiles.forEach(file -> documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, file.getId(),
                    DocumentApprovalLogOperation.add, DocumentApprovalLogOperationType.attachmentFile, null)));
        }
        //上传的文件列表
        documentApproval.setAttachmentFiles(attachmentFiles);
        documentApproval.setUser(usernameList.get(0));
        documentApprovalDao.save(documentApproval);
        List<DocumentApprovalNode> nodes = new ArrayList<>();
        DocumentApprovalNode documentApprovalNode;
        for (int i = 0; i < usernameList.size(); i++) {
            documentApprovalNode = new DocumentApprovalNode();
            documentApprovalNode.setStatus(i == 0 ? "Approval" : "NotStarted");
            documentApprovalNode.setUser(usernameList.get(i));
            documentApprovalNode.setSort(i);
            documentApprovalNode.setDocumentApproval(documentApproval);
            nodes.add(documentApprovalNode);
        }
        documentApproval.setDocumentApprovalNodes(nodes);
        documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, documentApproval.getId(),
                DocumentApprovalLogOperation.add, DocumentApprovalLogOperationType.documentApproval, null));
        messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "submitForApproval",
                "您提交的文件审批已进入审批流程");
        return documentApprovalDao.save(documentApproval);
    }

    /**
     * 同意审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval completedApproval(DocumentApproval documentApproval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<DocumentApprovalNode> nodes = documentApproval.getDocumentApprovalNodes();
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
                if(user != null && "Y".equals(user.getStatus()) && "N".equals(user.getIsDelete())){
                    documentApproval.setUser(userName);
                    node.setStatus("Approval");
                    break;
                }else if(i == (size - 1)){
                    documentApproval.setStatus("Completed");
                    messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "completedApproval",
                            "您提交的文件审批已经审核通过");
                }
                node.setStatus("NotStarted");
            } else if (node.getUser().equals(username)) {
                node.setStatus("Completed");
                node.setCompletedTime(LocalDateTime.now());
                //最后的审批者
                if (i == (size - 1)) {
                    documentApproval.setStatus("Completed");
                    messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "completedApproval",
                            "您提交的文件审批已经审核通过");
                } else {
                    needNextUserApproval = true;
                    messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "completedApproval",
                             String.format("【%s】已经同意您提交的文件审批", username));
                }
                messageNotificationService.releaseMessage(node.getUser(), "rejectedApproval",
                        String.format("您同意了用户【%s】提交的文件审批", documentApproval.getInitiatorUsername()));
            }
        }
        documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, documentApproval.getId(),
                DocumentApprovalLogOperation.agree, DocumentApprovalLogOperationType.documentApproval, null));
        return documentApprovalDao.save(documentApproval);
    }

    /**
     * 重新审批（被拒绝）
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval reApprove(DocumentApproval documentApproval) {
        documentApproval.setStatus("Approval");
        documentApproval.getDocumentApprovalNodes().forEach(node -> {
            if (documentApproval.getUser().equals(node.getUser())) {
                node.setStatus("Approval");
            }
        });
        documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, documentApproval.getId(),
                DocumentApprovalLogOperation.reApprove, DocumentApprovalLogOperationType.documentApproval, null));
        messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "reApprove", "您提交的文件审批已重新进入审批流程");
        return documentApprovalDao.save(documentApproval);
    }

    /**
     * 拒绝审批
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval rejectedApproval(DocumentApproval documentApproval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<DocumentApprovalNode> nodes = documentApproval.getDocumentApprovalNodes();
        DocumentApprovalNode node;
        for (DocumentApprovalNode documentApprovalNode : nodes) {
            node = documentApprovalNode;
            if (node.getUser().equals(username)) {
                node.setStatus("Rejected");
                documentApproval.setUser(node.getUser());
                node.setCompletedTime(LocalDateTime.now());
                documentApproval.setStatus("Rejected");
                messageNotificationService.releaseMessage(node.getUser(), "rejectedApproval",
                        String.format("您已经拒绝用户【%s】提交的文件审批", documentApproval.getInitiatorUsername()));
                break;
            }
        }
        documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, documentApproval.getId(),
                DocumentApprovalLogOperation.rejected, DocumentApprovalLogOperationType.documentApproval, null));
        messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "rejectedApproval", "您提交的文件审批未通过审核。");
        return documentApprovalDao.save(documentApproval);
    }

    /**
     * 检查用户是否有正在进行中的审批
     */
    public void checkUserHaveApproval(SysUser user){
        if(documentApprovalDao.checkUserHaveApproval(user.getUsername()) > 0){
            String error = String.format("当前用户[%s]有正在进行中的审批，请先结束审批", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }else if(documentApprovalDao.checkUserNeedApproval(user.getUsername()) > 0){
            String error = String.format("当前用户[%s]有需要审批的文件，请先审批完成", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }

    }

    public DocumentApprovalLog documentApprovalLog(DocumentApproval documentApproval, Long operationId, DocumentApprovalLogOperation operation,
                                                   DocumentApprovalLogOperationType operationType, String message) {
        DocumentApprovalLog log = new DocumentApprovalLog();
        log.setUsername(sysUserService.currentlyLoggedInUser());
        log.setOperation(operation.name());
        log.setOperationId(operationId);
        log.setDocumentApproval(documentApproval);
        log.setOperationType(operationType.name());
        log.setOperationMessage(message);
        return log;
    }

    /**
     * 添加评论
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentApproval addComment(DocumentApproval documentApproval, String message) {
        DocumentApprovalComment comment = new DocumentApprovalComment();
        comment.setDocumentApproval(documentApproval);
        comment.setIsDelete("N");
        comment.setMessage(message);
        documentApproval.getDocumentApprovalComments().add(comment);
        documentApproval.getDocumentApprovalLogs().add(documentApprovalLog(documentApproval, documentApproval.getId(),
                DocumentApprovalLogOperation.add, DocumentApprovalLogOperationType.documentApprovalComments, message));
        messageNotificationService.releaseMessage(documentApproval.getInitiatorUsername(), "addComment", "您提交的文件审批有了新的评论。");
        return documentApprovalDao.save(documentApproval);
    }

}

enum DocumentApprovalLogOperation {
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

enum DocumentApprovalLogOperationType {
    /**
     * 附件
     */
    attachmentFile,
    /**
     * 审批
     */
    documentApproval,
    /**
     * 评论
     */
    documentApprovalComments
}


