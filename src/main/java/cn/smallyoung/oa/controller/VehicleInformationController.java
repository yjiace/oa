package cn.smallyoung.oa.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.smallyoung.oa.entity.CarRecord;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;
import cn.smallyoung.oa.entity.VehicleInformation;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.VehicleInformationService;
import cn.smallyoung.oa.vo.VehicleInformationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author smallyoung
 * @data 2020/11/18
 */
@Slf4j
@RestController
@ResponseResultBody
@Api(tags = "车辆管理")
@RequestMapping("/vehicle/information")
public class VehicleInformationController {

    @Resource
    private VehicleInformationService vehicleInformationService;

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @ApiOperation(value = "分页查询车辆信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    @PreAuthorize("hasRole('ROLE_ VEHICLE') or hasRole('ROLE_ VEHICLE_FIND')")
    public Page<VehicleInformation> findAll(@RequestParam(defaultValue = "1") Integer page,
                                            HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return vehicleInformationService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 根据ID查询车辆详情
     *
     * @param id  车辆主键ID
     */
    @GetMapping(value = "findById")
    @ApiOperation(value = "根据ID查询车辆详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "车辆主键ID", dataType = "Long")
    })
    @PreAuthorize("hasRole('ROLE_ VEHICLE') or hasRole('ROLE_ VEHICLE_FIND')")
    public VehicleInformation findById(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        return vehicleInformationService.findOne(id);
    }

    /**
     * 新增车辆
     */
    @PostMapping(value = "save")
    @ApiOperation(value = "新增车辆")
    @PreAuthorize("hasRole('ROLE_VEHICLE') or hasRole('ROLE_VEHICLE_SAVEANDUPDATE')")
    @SystemOperationLog(module = "车辆管理", methods = "新增车辆", way = SysOperationLogWayEnum.RecordOnly)
    public VehicleInformation save(VehicleInformationVO vehicleInformationVO) {
        VehicleInformation vehicleInformation = new VehicleInformation();
        BeanUtil.copyProperties(vehicleInformationVO, vehicleInformation);
        vehicleInformation.setIsDelete("N");
        return vehicleInformationService.save(vehicleInformation);
    }

    /**
     * 编辑车辆
     */
    @PostMapping(value = "update")
    @ApiOperation(value = "编辑车辆")
    @PreAuthorize("hasRole('ROLE_VEHICLE') or hasRole('ROLE_VEHICLE_SAVEANDUPDATE')")
    @SystemOperationLog(module = "车辆管理", methods = "编辑车辆", serviceClass = VehicleInformationService.class, queryMethod = "findOne",
            parameterType = "Long", parameterKey = "vehicleInformationVO.id", way = SysOperationLogWayEnum.RecordBeforeAndAfterChanges)
    public VehicleInformation update(VehicleInformationVO vehicleInformationVO) {
        if (vehicleInformationVO == null || vehicleInformationVO.getId() == null) {
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = vehicleInformationService.findOne(vehicleInformationVO.getId());
        if (vehicleInformation == null) {
            String error = String.format("根据ID【%s】没有查询到该车", vehicleInformationVO.getId());
            log.error(error);
            throw new RuntimeException(error);
        }
        BeanUtil.copyProperties(vehicleInformationVO, vehicleInformation);
        return vehicleInformationService.save(vehicleInformation);
    }

    /**
     * 车辆记录
     *
     * @param id        车辆id
     * @param remarks   备注
     * @param operation 操作 ReviewCar：申请用车；VehicleDeparture：车辆离场；ReturnVehicle：归还车辆
     */
    @PostMapping("createVehicleRecord")
    @ApiOperation(value = "车辆记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "车辆id", dataType = "Long"),
            @ApiImplicitParam(name = "remarks", value = "备注", dataType = "String"),
            @ApiImplicitParam(name = "operation", value = "操作 ReviewCar：申请用车；VehicleDeparture：车辆离场；ReturnVehicle：归还车辆", dataType = "String")
    })
    @PreAuthorize("hasRole('ROLE_VEHICLE') or hasRole('ROLE_VEHICLE_RECORD')")
    public CarRecord createVehicleRecord(Long id, String remarks, String operation) {
        if (id == null || !VehicleInformationService.VEHICLE_INFORMATION_OPERATION.contains(operation)) {
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = vehicleInformationService.findOne(id);
        String isDelete = "Y";
        if (vehicleInformation == null || isDelete.equals(vehicleInformation.getIsDelete())) {
            String error = String.format("根据ID【%s】没有查询到该车", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return vehicleInformationService.createVehicleRecord(vehicleInformation, remarks, operation);
    }

    /**
     * 分页查询车辆记录
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAllCarRecord")
    @ApiOperation(value = "分页查询车辆记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    @PreAuthorize("hasRole('ROLE_ VEHICLE') or hasRole('ROLE_VEHICLE_RECORD_FIND')")
    public Page<CarRecord> findAllCarRecord(@RequestParam(defaultValue = "1") Integer page,
                                            HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return vehicleInformationService.findAllCarRecord(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime")));
    }

    //todo 每天18点生成Excel

}
