package cn.smallyoung.oa.controller;

import cn.smallyoung.oa.entity.SysRole;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.SysRoleService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @data 2020/10/31
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/role")
@Api(tags = "角色模块")
public class SysRoleController extends BaseController<SysRole, Long>{

    @Resource
    private SysRoleService sysRoleService;

    @PostMapping(value = "save")
    @ApiOperation(value = "保存用户")
    @ApiOperationSupport(ignoreParameters = {"permissions", "creator", "createTime", "updater", "updateTime", "isDelete", "users"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "角色名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "comments", value = "角色备注", dataType = "String")
    })
    public SysRole save(SysRole role, HttpServletResponse response) {
        Long userId = Long.parseLong(response.getHeader("userId"));
        log.info("用户{}保存角色{}", userId, role);
        if (role.getId() == null) {
            role.setCreator(userId);
            role.setCreateTime(LocalDateTime.now());
        } else {
            SysRole oldRole = sysRoleService.findOne(role.getId());
            role.setCreator(oldRole.getCreator());
            role.setCreateTime(oldRole.getCreateTime());
            role.setPermissions(oldRole.getPermissions());
        }
        role.setUpdateTime(LocalDateTime.now());
        role.setUpdater(userId);
        role.setIsDelete("N");
        return sysRoleService.save(role);
    }
}
