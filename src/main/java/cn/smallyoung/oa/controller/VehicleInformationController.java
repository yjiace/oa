package cn.smallyoung.oa.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
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
import java.util.Arrays;
import java.util.Map;

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
    public Page<VehicleInformation> findAll(@RequestParam(defaultValue = "1") Integer page,
                                            HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "search_");
        map.put("AND_EQ_isDelete", "N");
        return vehicleInformationService.findAll(map, PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 根据ID查询车辆详情
     *
     * @param id 车辆主键ID
     */
    @GetMapping(value = "findById")
    @ApiOperation(value = "根据ID查询车辆详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "车辆主键ID", dataType = "Long")
    })
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plateNumber", value = "车牌号", dataType = "String"),
            @ApiImplicitParam(name = "company", value = "所属单位", dataType = "String"),
            @ApiImplicitParam(name = "model", value = "车辆型号", dataType = "String")
    })
    @PreAuthorize("hasRole('ROLE_VEHICLE') or hasRole('ROLE_VEHICLE_SAVE')")
    @SystemOperationLog(module = "车辆管理", methods = "新增车辆")
    public VehicleInformation save(VehicleInformationVO vehicleInformationVO) {
        VehicleInformation vehicleInformation = new VehicleInformation();
        BeanUtil.copyProperties(vehicleInformationVO, vehicleInformation, CopyOptions.create().setIgnoreNullValue(true));
        vehicleInformation.setIsDelete("N");
        vehicleInformation.setStatus(VehicleInformationService.VEHICLE_INFORMATION_STATUS.get(0));
        return vehicleInformationService.save(vehicleInformation);
    }

    /**
     * 编辑车辆
     */
    @PostMapping(value = "update")
    @ApiOperation(value = "编辑车辆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "车辆主键ID", dataType = "Long"),
            @ApiImplicitParam(name = "plateNumber", value = "车牌号", dataType = "String"),
            @ApiImplicitParam(name = "company", value = "所属单位", dataType = "String"),
            @ApiImplicitParam(name = "model", value = "车辆型号", dataType = "String")
    })
    @PreAuthorize("hasRole('ROLE_VEHICLE') or hasRole('ROLE_VEHICLE_SAVE')")
    @SystemOperationLog(module = "车辆管理", methods = "编辑车辆", serviceClass = VehicleInformationService.class, queryMethod = "findOne",
            parameterType = "Long", parameterKey = "vehicleInformationVO.id", way = SysOperationLogWayEnum.UserAfter)
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
        BeanUtil.copyProperties(vehicleInformationVO, vehicleInformation, CopyOptions.create().setIgnoreNullValue(true));
        return vehicleInformationService.save(vehicleInformation);
    }

    /**
     * 删除车辆
     *
     * @param id 车辆id
     */
    @PostMapping("delVehicleRecord")
    @ApiOperation(value = "删除车辆")
    @PreAuthorize("hasRole('ROLE_VEHICLE') or hasRole('ROLE_VEHICLE_DELETE')")
    @ApiImplicitParam(name = "id", value = "车辆id", required = true, dataType = "Long")
    @SystemOperationLog(module = "车辆管理", methods = "删除车辆", serviceClass = VehicleInformationService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public VehicleInformation delVehicleRecord(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = vehicleInformationService.findOne(id);
        if (vehicleInformation == null) {
            String error = String.format("根据ID【%s】没有查询到该车", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        vehicleInformation.setIsDelete("Y");
        return vehicleInformationService.save(vehicleInformation);
    }

    /**
     * 车辆离场和归还
     *
     * @param id        车辆id
     * @param operation 操作 VehicleDeparture：车辆离场；ReturnVehicle：归还车辆
     */
    @PostMapping("updateVehicleStatus")
    @ApiOperation(value = "车辆离场和归还")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "车辆id", dataType = "Long"),
            @ApiImplicitParam(name = "operation", value = "操作，VehicleDeparture：车辆离场；ReturnVehicle：归还车辆", dataType = "String")
    })
    public VehicleInformation updateVehicleStatus(Long id, String operation) {
        if (StrUtil.isBlank(operation) || !Arrays.asList("VehicleDeparture", "ReturnVehicle").contains(operation)) {
            throw new NullPointerException("参数错误");
        }
        VehicleInformation vehicleInformation = vehicleInformationService.findOne(id);
        String isDelete = "Y";
        if (vehicleInformation == null || isDelete.equals(vehicleInformation.getIsDelete())) {
            String error = String.format("根据ID【%s】没有查询到该车", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return vehicleInformationService.updateVehicleStatus(vehicleInformation, operation);
    }
}
