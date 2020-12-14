package cn.smallyoung.oa.service;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.VehicleInformationDao;
import cn.smallyoung.oa.entity.VehicleInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author smallyoung
 * @data 2020/11/18
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class VehicleInformationService extends BaseService<VehicleInformation, Long> {


    /**
     * 状态，NotInUse：未使用，Approval：审批中，NotLeaving：未离场，NotReturned：未归还
     */
    public final static List<String> VEHICLE_INFORMATION_STATUS = Arrays.asList("NotInUse", "Approval", "NotLeaving", "NotReturned", "NotInUse");

    /**
     * 操作 ReviewCar：申请用车；CompletedApproval：同意审批；VehicleDeparture：车辆离场；ReturnVehicle：归还车辆
     */
    public final static List<String> VEHICLE_INFORMATION_OPERATION = Arrays.asList("ReviewCar", "CompletedApproval", "VehicleDeparture", "ReturnVehicle");

    public final static Map<String, String> VEHICLE_INFORMATION_DESCRIPTION = new HashMap<String, String>() {

        private static final long serialVersionUID = 4299492764225991279L;

        {
            //未使用---车辆离场
            put("NotInUseVehicleDeparture", "当前车辆【%s】还未申请使用，暂时无法操作离场。");
            //未使用---归还车辆
            put("NotInUseReturnVehicle", "当前车辆【%s】还未申请使用，暂时无法操作归还。");
            //未使用---同意审批
            put("NotInUseCompletedApproval", "当前车辆【%s】还未提交用车申请，暂时通过本次审批。");
            //未离场---申请用车
            put("NotLeavingReviewCar", "当前车辆【%s】已申请使用，暂时无法申请用车。");
            //未离场---归还车辆
            put("NotLeavingReturnVehicle", "当前车辆【%s】还未离场，暂时无法操作归还。");
            //未离场---同意审批
            put("NotLeavingCompletedApproval", "当前车辆【%s】还未已离场，暂时通过本次审批。");
            //未归还---申请用车
            put("NotReturnedReviewCar", "当前车辆【%s】还未归还，暂时无法申请用车。");
            //未归还---车辆离场
            put("NotReturnedVehicleDeparture", "当前车辆【%s】已离场，无需重复操作离场。");
            //未归还---同意审批
            put("NotReturnedCompletedApproval", "当前车辆【%s】还未归还，暂时通过本次审批。");
            //审批中---申请用车
            put("ApprovalReviewCar", "当前车辆【%s】正在审批中，暂时无法申请用车。");
            //审批中---车辆离场
            put("ApprovalVehicleDeparture", "当前车辆【%s】正在审批中，暂时不能操作离场。");
            //审批中---归还车辆
            put("ApprovalReturnVehicle", "当前车辆【%s】正在审批中，暂时不能操作归还。");
        }
    };

    @Resource
    private VehicleInformationDao vehicleInformationDao;

    @Override
    public VehicleInformation findOne(Long id) {
        return super.findOne(id);
    }

    public VehicleInformation findByPlateNumber(String plateNumber) {
        return StrUtil.isNotBlank(plateNumber) ? vehicleInformationDao.findByPlateNumber(plateNumber) : null;
    }

    public void updateVehicleStatus(String plateNumber, String operation){
        updateVehicleStatus(findByPlateNumber(plateNumber), operation);
    }

    @Transactional(rollbackFor = Exception.class)
    public VehicleInformation updateVehicleStatus(VehicleInformation vehicleInformation, String operation) {
        if(vehicleInformation == null){
            throw  new RuntimeException("该车辆不存在");
        }
        if (VEHICLE_INFORMATION_STATUS.indexOf(vehicleInformation.getStatus()) != VEHICLE_INFORMATION_OPERATION.indexOf(operation)) {
            String error = String.format(VEHICLE_INFORMATION_DESCRIPTION.get(vehicleInformation.getStatus() + operation), vehicleInformation.getPlateNumber());
            log.error(error);
            throw new RuntimeException(error);
        }
        vehicleInformation.setStatus(VEHICLE_INFORMATION_STATUS.get(VEHICLE_INFORMATION_OPERATION.indexOf(operation) + 1));
        return vehicleInformationDao.save(vehicleInformation);
    }

}
