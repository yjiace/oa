package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.VehicleApprovalDao;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.entity.VehicleApproval;
import cn.smallyoung.oa.entity.VehicleApprovalNode;
import cn.smallyoung.oa.entity.VehicleInformation;
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
public class VehicleApprovalService extends BaseService<VehicleApproval, Long> {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private VehicleApprovalDao vehicleApprovalDao;
    @Resource
    private VehicleInformationService vehicleInformationService;
    @Resource
    private MessageNotificationService messageNotificationService;

    @Override
    public VehicleApproval findOne(Long id) {
        return super.findOne(id);
    }

    /**
     * 查询需要我审批的审批
     *
     * @param page  页码
     * @param limit 页数
     */
    public Page<VehicleApproval> findAllApprovalRequired(Integer page, Integer limit) {
        String username = sysUserService.currentlyLoggedInUser();
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "sort"));
        long count = vehicleApprovalDao.countApprovalRequired(username);
        if (count <= 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<VehicleApproval> approvals = vehicleApprovalDao.findAllApprovalRequired(username, limit, (page - 1) * limit);
        return new PageImpl<>(approvals, pageable, count);
    }

    /**
     * 撤回审批
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleApproval withdrawalOfApproval(VehicleApproval approval) {
        List<VehicleApprovalNode> nodes = approval.getVehicleApprovalNodes();
        if (CollUtil.isNotEmpty(nodes)) {
            nodes.forEach(n -> n.setStatus("Withdrawn"));
        }
        approval.setStatus("Withdrawn");
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "withdrawalOfApproval", "已经成功撤销您提交的车辆审批");
        vehicleApprovalDao.save(approval);
        //修改车辆状态
        VehicleInformation vehicleInformation = vehicleInformationService.findByPlateNumber(approval.getVehicleNumber());
        vehicleInformation.setStatus(VehicleInformationService.VEHICLE_INFORMATION_STATUS.get(0));
        vehicleInformationService.save(vehicleInformation);
        return approval;
    }

    /**
     * 提交审批
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleApproval submitForApproval(VehicleApproval vehicleApproval) {
        vehicleInformationService.updateVehicleStatus(vehicleApproval.getVehicleNumber(), VehicleInformationService.VEHICLE_INFORMATION_OPERATION.get(0));
        List<String> usernameList = vehicleApproval.getUsername().stream().distinct().collect(Collectors.toList());
        //查询用户集合
        List<SysUser> userList = sysUserService.findByUsernameIn(usernameList);
        if (usernameList.size() != userList.size()) {
            List<String> checkUser = userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
            String error = String.format("用户【%s】为无效用户", usernameList.stream()
                    .filter(s -> !checkUser.contains(s)).collect(Collectors.joining(",")));
            log.error(error);
            throw new RuntimeException(error);
        }
        vehicleApproval.setInitiatorUsername(sysUserService.currentlyLoggedInUser());
        vehicleApproval.setStatus("Approval");

        vehicleApprovalDao.save(vehicleApproval);
        List<VehicleApprovalNode> nodes = new ArrayList<>();
        VehicleApprovalNode approvalNode;
        for (int i = 0; i < usernameList.size(); i++) {
            approvalNode = new VehicleApprovalNode();
            approvalNode.setStatus(i == 0 ? "Approval" : "NotStarted");
            approvalNode.setUser(usernameList.get(i));
            approvalNode.setSort(i);
            approvalNode.setVehicleApproval(vehicleApproval);
            nodes.add(approvalNode);
        }
        vehicleApproval.setNode(nodes.get(0));
        vehicleApproval.setVehicleApprovalNodes(nodes);
        messageNotificationService.releaseMessage(vehicleApproval.getInitiatorUsername(), "submitForApproval", "您提交的车辆审批已进入审批流程");
        messageNotificationService.releaseMessage(usernameList.get(0), "submitForApproval", "有需要您审批的车辆审批，请查看");
        vehicleApprovalDao.save(vehicleApproval);
        return vehicleApproval;
    }

    /**
     * 同意审批
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleApproval completedApproval(VehicleApproval approval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<VehicleApprovalNode> nodes = approval.getVehicleApprovalNodes();
        VehicleApprovalNode node;
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
                    messageNotificationService.releaseMessage(user.getUsername(), "completedApproval", "有需要您的车辆审批，请查看");
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
                    approval.setApprovedChief(username);
                    messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "completedApproval",
                            "您提交的审批已经审核通过");
                } else {
                    needNextUserApproval = true;
                    messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "completedApproval",
                            String.format("【%s】已经同意您提交的审批", username));
                    vehicleInformationService.updateVehicleStatus(approval.getVehicleNumber(), VehicleInformationService.VEHICLE_INFORMATION_OPERATION.get(1));
                }
                messageNotificationService.releaseMessage(node.getUser(), "rejectedApproval",
                        String.format("您同意了用户【%s】提交的审批", approval.getInitiatorUsername()));
            }
        }
        vehicleApprovalDao.save(approval);
        return approval;
    }

    /**
     * 重新审批（被拒绝）
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleApproval reApprove(VehicleApproval approval) {
        approval.setStatus("Approval");
        approval.getVehicleApprovalNodes().forEach(node -> {
            if (approval.getNode().getUser().equals(node.getUser())) {
                node.setStatus("Approval");
                messageNotificationService.releaseMessage(node.getUser(), "reApprove", "有需要您审批的车辆审批，请查看");
            }
        });
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "reApprove", "您提交的车辆审批已重新进入审批流程");
        vehicleApprovalDao.save(approval);
        vehicleInformationService.updateVehicleStatus(approval.getVehicleNumber(), VehicleInformationService.VEHICLE_INFORMATION_OPERATION.get(0));
        return approval;
    }

    /**
     * 拒绝审批
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleApproval rejectedApproval(VehicleApproval approval) {
        String username = sysUserService.currentlyLoggedInUser();
        List<VehicleApprovalNode> nodes = approval.getVehicleApprovalNodes();
        VehicleApprovalNode node;
        for (VehicleApprovalNode approvalNode : nodes) {
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
        messageNotificationService.releaseMessage(approval.getInitiatorUsername(), "rejectedApproval", "您提交的车辆审批未通过审核。");
        vehicleApprovalDao.save(approval);
        //修改车辆状态
        VehicleInformation vehicleInformation = vehicleInformationService.findByPlateNumber(approval.getVehicleNumber());
        vehicleInformation.setStatus(VehicleInformationService.VEHICLE_INFORMATION_STATUS.get(0));
        vehicleInformationService.save(vehicleInformation);
        return approval;
    }

    /**
     * 检查用户是否有正在进行中的审批
     */
    public void checkUserHaveApproval(SysUser user) {
        if (vehicleApprovalDao.checkUserHaveApproval(user.getUsername()) > 0) {
            String error = String.format("当前用户[%s]有正在进行中的车辆审批，请先结束审批", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        } else if (vehicleApprovalDao.countApprovalRequired(user.getUsername()) > 0) {
            String error = String.format("当前用户[%s]有需要审批的车辆，请先审批完成", user.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }
    }
}