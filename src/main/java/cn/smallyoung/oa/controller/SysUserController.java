package cn.smallyoung.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.SysRoleService;
import cn.smallyoung.oa.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/user")
@Api(tags = "用户模块")
public class SysUserController {

    @Value("${default.password}")
    private String defaultPassword;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @ApiOperation(value = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_FIND')")
    public Page<SysUser> findAll(@RequestParam(defaultValue = "1") Integer page,
                                 HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return sysUserService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 根据用户名查询详细信息
     *
     * @param id 主键
     */
    @GetMapping(value = "findByUsername")
    @ApiOperation(value = "根据id查询")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_FIND')")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long")
    public SysUser findById(Long id) {
        if(id == null){
            throw new NullPointerException("参数错误");
        }
        return sysUserService.findById(id).orElse(null);
    }

    /**
     * 检查用户是否存在
     *
     * @param username 用户名
     */
    @GetMapping("checkUsername")
    @ApiOperation(value = "检查用户名是否存在")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_SAVE')")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    public boolean checkUsername(String username) {
        return StrUtil.isBlank(username) || sysUserService.findByUsername(username) == null;
    }

    /**
     * 保存用户
     *
     * @param username 用户名
     * @param name     姓名
     * @param phone    手机号
     * @param mobile   电话
     */
    @PostMapping(value = "save")
    @ApiOperation(value = "新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String"),
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_SAVE')")
    @SystemOperationLog(module="管理用户",methods="新增用户",serviceClass=SysUserService.class,
            queryMethod="findByUsername",parameterType="String",parameterKey="username")
    public SysUser save(String username, String name, String phone, String mobile) throws Exception {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(username);
        if(user != null){
            throw new Exception("用户已存在");
        }
        user = new SysUser();
        user.setUsername(username);
        user.setStatus("Y");
        user.setIsDelete("N");
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setName(name);
        user.setPhone(phone);
        user.setMobile(mobile);
        return sysUserService.save(user);
    }

    /**
     * 更新用户
     *
     * @param id 主键
     * @param name     姓名
     * @param phone    手机号
     * @param mobile   电话
     */
    @PostMapping(value = "update")
    @ApiOperation(value = "编辑用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String"),
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_UPDATE')")
    @SystemOperationLog(module="管理用户",methods="编辑用户",serviceClass=SysUserService.class,
            queryMethod="findOne",parameterType="Long",parameterKey="id")
    public SysUser update(Long id, String name, String phone, String mobile) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(id);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with id '%s'.", id));
        }
        user.setName(name);
        user.setPhone(phone);
        user.setMobile(mobile);
        return sysUserService.save(user);
    }

    /**
     * 更改用户状态
     *
     * @param id 主键
     * @param status   需要更改用户的状态：Y，启用；N，禁用
     */
    @PostMapping(value = "updateStatus")
    @ApiOperation(value = "修改用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态：Y，启用；N，禁用", required = true, dataType = "String")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_UPDATE_STATUS')")
    @SystemOperationLog(module="管理用户",methods="更改用户状态",serviceClass=SysUserService.class,
            queryMethod="findOne",parameterType="Long",parameterKey="id")
    public SysUser updateStatus(Long id, String status) {
        if (id == null || StrUtil.hasBlank(status)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(id);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with id '%s'.", id));
        }
        user.setStatus(status);
        return sysUserService.save(user);
    }

    /**
     * 重置密码
     *
     * @param id 主键
     */
    @PostMapping(value = "resetPassword")
    @ApiOperation(value = "重置密码")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_RESET_PASSWORD')")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    @SystemOperationLog(module="管理用户",methods="重置密码",serviceClass=SysUserService.class,
            queryMethod="findOne",parameterType="Long",parameterKey="id")
    public SysUser resetPassword(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(id);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with id '%s'.", id));
        }
        user.setPassword(passwordEncoder.encode(defaultPassword));
        return sysUserService.save(user);
    }

    /**
     * 修改密码
     *
     * @param id 主键
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @PostMapping(value = "updatePassword")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    @PreAuthorize("principal.username.equals(#username)")
    public Integer updatePassword(Long id, String oldPassword, String newPassword) {
        if (StrUtil.hasBlank(oldPassword, newPassword) || id == null) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(id);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with id '%s'.", id));
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UsernameNotFoundException(String.format("Wrong password with password '%s'.", newPassword));
        }
        return sysUserService.updatePassword(user, newPassword);
    }

    /**
     * 删除
     *
     * @param id 主键
     */
    @DeleteMapping(value = "delete")
    @ApiOperation(value = "删除用户")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_DELETE')")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    @SystemOperationLog(module="管理用户",methods="删除用户",serviceClass=SysUserService.class,
            queryMethod="findOne",parameterType="Long",parameterKey="id")
    public SysUser deleteUser(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(id);
        if(user == null){
            throw new UsernameNotFoundException(String.format("No user found with id '%s'.", id));
        }
        user.setIsDelete("Y");
        return sysUserService.save(user);
    }

    /**
     * 设置用户角色
     *
     * @param id 主键
     * @param roles    角色id集合，逗号分割
     */
    @PostMapping("updateRole")
    @ApiOperation(value = "设置用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "roles", value = "角色ID集合,逗号分割", required = true, dataType = "String"),
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_UPDATE_ROLE')")
    @SystemOperationLog(module="管理用户",methods="设置用户角色",serviceClass=SysUserService.class,
            queryMethod="findOne",parameterType="Long",parameterKey="id")
    public SysUser updateRole(Long id, String roles) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findOne(id);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with id '%s'.", id));
        }
        if (StrUtil.isNotBlank(roles)) {
            user.setRoles(sysRoleService.findByIdInAndIsDelete(Stream.of(roles.split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList())));
        } else {
            user.setRoles(null);
        }
        return sysUserService.save(user);
    }
}
