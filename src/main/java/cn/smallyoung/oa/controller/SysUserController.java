package cn.smallyoung.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.SysRoleService;
import cn.smallyoung.oa.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/user")
@Api(tags = "用户模块")
public class SysUserController extends BaseController<SysUser, String> {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("checkUsername")
    @ApiOperation(value = "检查用户名是否存在")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    public boolean checkUsername(String username) {
        return StrUtil.isBlank(username) || sysUserService.findOne(username) == null;
    }

    /**
     * 保存用户或者更新用户
     *
     * @param username 用户名
     * @param name     姓名
     * @param phone    手机号
     * @param mobile   电话
     */
    @PostMapping(value = "save")
    @ApiOperation(value = "保存用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String"),
    })
    public SysUser save(String username, String name, String phone, String mobile) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.save(username, name, phone, mobile);
    }

    /**
     * 更改用户状态
     *
     * @param username 需要更改状态的用户名
     * @param status   需要更改用户的状态：Y，启用；N，禁用
     */
    @PostMapping(value = "updateStatus")
    @ApiOperation(value = "修改用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态：Y，启用；N，禁用", required = true, dataType = "String")
    })
    public Integer updateStatus(String username, String status) {
        if (StrUtil.hasBlank(status, username)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.updateStatus(username, status);
    }

    /**
     * 重置密码
     *
     * @param username 需要重置密码的用户名
     */
    @PostMapping(value = "resetPassword")
    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    public Integer resetPassword(String username) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.resetPassword(username);
    }

    /**
     * 修改密码
     *
     * @param username    需要修改密码的用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @PostMapping(value = "updatePassword")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    public Integer updatePassword(String username, String oldPassword, String newPassword) {
        if (StrUtil.hasBlank(oldPassword, newPassword, username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UsernameNotFoundException(String.format("Wrong password with password '%s'.", newPassword));
        }
        return sysUserService.updatePassword(user, newPassword);
    }

    /**
     * 删除
     *
     * @param username 需要删除的用户名
     */
    @DeleteMapping(value = "delete")
    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    public Integer deleteUser(String username) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.updateIsDelete(username, "Y");
    }

    @PostMapping("updateRole")
    @ApiOperation(value = "设置用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "roles[]", value = "角色ID集合", required = true, dataType = "List"),
    })
    public SysUser updateRole(String username, @RequestParam(value = "roles[]") List<Long> roles) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        user.setRoles(sysRoleService.findByIdInAndIsDelete(roles));
        return sysUserService.save(user);
    }
}
