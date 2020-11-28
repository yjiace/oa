package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.base.specification.SimpleSpecificationBuilder;
import cn.smallyoung.oa.dao.CarRecordDao;
import cn.smallyoung.oa.dao.VehicleInformationDao;
import cn.smallyoung.oa.entity.Approval;
import cn.smallyoung.oa.entity.CarRecord;
import cn.smallyoung.oa.entity.CarRecordLog;
import cn.smallyoung.oa.entity.VehicleInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 操作 ReviewCar：申请用车；CompletedApproval：同意审批；VehicleDeparture：车辆离场；ReturnVehicle：归还车辆
     */
    public final static List<String> VEHICLE_INFORMATION_OPERATION = Arrays.asList("ReviewCar", "CompletedApproval", "VehicleDeparture", "ReturnVehicle");

    @Resource
    private CarRecordDao carRecordDao;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private VehicleInformationDao vehicleInformationDao;

    @Override
    public VehicleInformation findOne(Long id) {
        return super.findOne(id);
    }

    public Page<CarRecord> findAllCarRecord(Map<String, Object> map, Pageable pageable) {
        return carRecordDao.findAll(new SimpleSpecificationBuilder<CarRecord>(map).getSpecification(), pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public CarRecord createVehicleRecord(VehicleInformation vehicleInformation, String remarks, String operation) {
        int index = VEHICLE_INFORMATION_OPERATION.indexOf(operation);
        CarRecord carRecord;
        //申请用车
        if (index == 0 && vehicleInformation.getCurrentCarRecord() == null) {
            carRecord = new CarRecord();
            carRecord.setStatus(VEHICLE_INFORMATION_STATUS.get(index));
            carRecord.setRemarks(remarks);
            carRecord.setUsername(sysUserService.currentlyLoggedInUser());
            carRecord.setVehicleInformation(vehicleInformation);
            carRecordDao.save(carRecord);
        } else if (vehicleInformation.getCurrentCarRecord() != null) {
            carRecord = vehicleInformation.getCurrentCarRecord();
        } else {
            carRecord = new CarRecord();
            carRecord.setStatus(VEHICLE_INFORMATION_STATUS.get(0));
        }
        //校验状态
        checkStatus(vehicleInformation.getName(), carRecord.getStatus(), operation);
        //保存数据
        CarRecordLog carRecordLog = new CarRecordLog();
        carRecordLog.setCarRecord(carRecord);
        carRecordLog.setRemarks(remarks);
        carRecordLog.setType(operation);
        carRecordLog.setUsername(sysUserService.currentlyLoggedInUser());
        carRecord.getCarRecordLogs().add(carRecordLog);
        carRecord.setStatus(VEHICLE_INFORMATION_STATUS.get(index + 1));
        carRecordDao.save(carRecord);
        if (index == 3) {
            //归还车辆
            vehicleInformation.setCurrentCarRecord(null);
        } else {
            vehicleInformation.setCurrentCarRecord(carRecord);
        }
        vehicleInformationDao.save(vehicleInformation);
        return carRecord;
    }


    /**
     * 校验当前车辆状态是否允许相对于的操作
     *
     * @param status    状态，NotInUse：未使用，NotLeaving：未离场，NotReturned：未归还
     * @param operation 操作 ReviewCar：申请用车；VehicleDeparture：车辆离场；ReturnVehicle：归还车辆
     */
    private void checkStatus(String name, String status, String operation) {
        if (VEHICLE_INFORMATION_STATUS.indexOf(status) == VEHICLE_INFORMATION_OPERATION.indexOf(operation)) {
            return;
        }
        String error = String.format(VEHICLE_INFORMATION_DESCRIPTION.get(status + operation), name);
        log.error(error);
        throw new RuntimeException(error);
    }

    /**
     * 撤回审批
     */
    @Transactional(rollbackFor = Exception.class)
    public void withdrawalOfApproval(Approval approval) {
        String vehicle = "vehicle";
        if(!vehicle.equals(approval.getType())){
            return;
        }
        if(approval.getVehicleId() == null){
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = this.findOne(approval.getVehicleId());
        CarRecord carRecord = vehicleInformation.getCurrentCarRecord();
        CarRecordLog carRecordLog = new CarRecordLog();
        carRecordLog.setCarRecord(carRecord);
        carRecordLog.setRemarks(approval.getRemarks());
        carRecordLog.setType("withdrawalOfApproval");
        carRecordLog.setUsername(sysUserService.currentlyLoggedInUser());
        carRecord.getCarRecordLogs().add(carRecordLog);
        carRecord.setStatus("withdrawalOfApproval");
        carRecordDao.save(carRecord);
        vehicleInformation.setCurrentCarRecord(null);
        vehicleInformationDao.save(vehicleInformation);
    }

    /**
     * 检查是否可以提交审批
     */
    public void checkApproval(Approval approval) {
        String vehicle = "vehicle";
        if(!vehicle.equals(approval.getType())){
            return;
        }
        if(approval.getVehicleId() == null){
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = this.findOne(approval.getVehicleId());
        if(vehicleInformation == null){
            String error = String.format("根据ID【%s】没有查询到该车", approval.getVehicleId());
            log.error(error);
            throw new RuntimeException(error);
        }
        if(vehicleInformation.getCurrentCarRecord() != null){
            String error = String.format("当前车辆【%s】已被占用", vehicleInformation.getId());
            log.error(error);
            throw new RuntimeException(error);
        }
    }

    /**
     * 成功提交审批
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Approval approval) {
        String vehicle = "vehicle";
        if(!vehicle.equals(approval.getType())){
            return;
        }
        if(approval.getVehicleId() == null){
            throw new NullPointerException("参数错误");
        }
        CarRecord carRecord = this.createVehicleRecord(this.findOne(approval.getVehicleId()), approval.getRemarks(), VEHICLE_INFORMATION_OPERATION.get(0));
        carRecord.setApproval(approval);
        carRecordDao.save(carRecord);
    }

    /**
     * 审批通过
     */
    @Transactional(rollbackFor = Exception.class)
    public void completedApproval(Approval approval) {
        String vehicle = "vehicle";
        if(!vehicle.equals(approval.getType())){
            return;
        }
        if(approval.getVehicleId() == null){
            throw new NullPointerException("参数错误");
        }
        this.createVehicleRecord(this.findOne(approval.getVehicleId()), approval.getRemarks(), VEHICLE_INFORMATION_OPERATION.get(1));
    }

    /**
     * 拒绝审批
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectedApproval(Approval approval){
        String vehicle = "vehicle";
        if(!vehicle.equals(approval.getType())){
            return;
        }
        if(approval.getVehicleId() == null){
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = this.findOne(approval.getVehicleId());
        CarRecord carRecord = vehicleInformation.getCurrentCarRecord();
        CarRecordLog carRecordLog = new CarRecordLog();
        carRecordLog.setCarRecord(carRecord);
        carRecordLog.setRemarks(approval.getRemarks());
        carRecordLog.setType("rejectedApproval");
        carRecordLog.setUsername(sysUserService.currentlyLoggedInUser());
        carRecord.getCarRecordLogs().add(carRecordLog);
        carRecord.setStatus("rejectedApproval");
        carRecordDao.save(carRecord);
        vehicleInformation.setCurrentCarRecord(null);
        vehicleInformationDao.save(vehicleInformation);
    }
}
