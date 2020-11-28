package cn.smallyoung.oa.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;
import cn.smallyoung.oa.entity.SysRole;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.ApprovalService;
import cn.smallyoung.oa.service.SysRoleService;
import cn.smallyoung.oa.service.SysUserService;
import cn.smallyoung.oa.vo.SysUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
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
    @Resource
    private ApprovalService approvalService;

    @GetMapping(value = "findAllUserNames")
    @ApiOperation(value = "查询所有用户名（无权限，审批选择）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    public Page<String> findAllUserNames(@RequestParam(defaultValue = "1") Integer page, HttpServletRequest request,
                                          @RequestParam(defaultValue = "10") Integer limit){
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "search_");
        map.put("AND_EQ_isDelete", "N");
        map.put("AND_EQ_status", "Y");
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime"));
        Page<SysUser> sysUserPage = sysUserService.findAll(map, pageable);
        List<String> userNames = sysUserPage.getContent().stream().map(SysUser::getUsername).collect(Collectors.toList());
        return new PageImpl<>(userNames, pageable, sysUserPage.getTotalElements());
    }

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
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "search_");
        map.put("AND_EQ_isDelete", "N");
        map.put("AND_EQ_status", "Y");
        return sysUserService.findAll(map,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 根据用户名查询详细信息
     *
     * @param username 用户名
     */
    @GetMapping(value = "findByUsername")
    @ApiOperation(value = "根据用户名查询")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_FIND') or authentication.principal.username.equals(#username)")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    public SysUser findById(String username) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.findByUsername(username);
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
        return StrUtil.isNotBlank(username) || sysUserService.findByUsername(username) != null;
    }

    /**
     * 保存用户
     */
    @PostMapping(value = "save")
    @ApiOperation(value = "新增用户")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_SAVE')")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String"),
            @ApiImplicitParam(name = "company", value = "单位", dataType = "String"),
            @ApiImplicitParam(name = "section", value = "科室", dataType = "String"),
            @ApiImplicitParam(name = "position", value = "职位", dataType = "String"),
            @ApiImplicitParam(name = "department", value = "部门", dataType = "String"),
    })
    @SystemOperationLog(module = "用户管理", methods = "新增用户", serviceClass = SysUserService.class, queryMethod = "findByUsername",
            parameterType = "String", parameterKey = "sysUserVO.username", way = SysOperationLogWayEnum.RecordTheAfter)
    public SysUser save(SysUserVO sysUserVO){
        if (sysUserVO == null || StrUtil.hasBlank(sysUserVO.getUsername())) {
            throw new NullPointerException("参数错误");
        }
        if(sysUserService.existsById(sysUserVO.getUsername())){
            String error = String.format("用户【%s】已存在", sysUserVO.getUsername());
            log.error(error);
            throw new RuntimeException(error);
        }
        SysUser user = new SysUser();
        BeanUtil.copyProperties(sysUserVO, user, CopyOptions.create().setIgnoreNullValue(true));
        user.setUsername(sysUserVO.getUsername());
        user.setStatus("Y");
        user.setIsDelete("N");
        user.setPassword(passwordEncoder.encode(defaultPassword));
        return sysUserService.save(user);
    }

    /**
     * 编辑用户
     *
     */
    @PostMapping(value = "update")
    @ApiOperation(value = "编辑用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String"),
            @ApiImplicitParam(name = "company", value = "单位", dataType = "String"),
            @ApiImplicitParam(name = "section", value = "科室", dataType = "String"),
            @ApiImplicitParam(name = "position", value = "职位", dataType = "String"),
            @ApiImplicitParam(name = "department", value = "部门", dataType = "String"),
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_SAVE') or authentication.principal.username.equals(#sysUserVO.username)")
    @SystemOperationLog(module = "用户管理", methods = "编辑用户", serviceClass = SysUserService.class,
            queryMethod = "findByUsername", parameterType = "String", parameterKey = "sysUserVO.username")
    public SysUser update(SysUserVO sysUserVO) {
        if (sysUserVO == null || StrUtil.hasBlank(sysUserVO.getUsername())) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(sysUserVO.getUsername());
        if(user == null){
            String error = String.format("根据用户名【%s】没有找到用户", sysUserVO.getUsername());
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        BeanUtil.copyProperties(sysUserVO, user, CopyOptions.create().setIgnoreNullValue(true));
        return sysUserService.save(user);
    }

    /**
     * 更改用户状态
     *
     * @param username 用户名
     * @param status 需要更改用户的状态：Y，启用；N，禁用
     */
    @PostMapping(value = "updateStatus")
    @ApiOperation(value = "修改用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态：Y，启用；N，禁用", required = true, dataType = "String")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_UPDATE_STATUS')")
    @SystemOperationLog(module = "用户管理", methods = "更改用户状态", serviceClass = SysUserService.class,
            queryMethod = "findByUsername", parameterType = "String", parameterKey = "username")
    public SysUser updateStatus(String username, String status) {
        if (StrUtil.hasBlank(status, username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(username);
        if (user == null) {
            String error = String.format("根据用户名【%s】没有找到用户", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        String isDelete  = "Y";
        if(isDelete.equals(user.getIsDelete())){
            String error = String.format("该用户【%s】已删除", username);
            log.error(error);
            throw new RuntimeException(error);
        }
        approvalService.checkUserHaveApproval(user);
        user.setStatus(status);
        return sysUserService.save(user);
    }

    /**
     * 重置密码
     *
     * @param username 用户名
     */
    @PostMapping(value = "resetPassword")
    @ApiOperation(value = "重置密码")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_RESET_PASSWORD')")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @SystemOperationLog(module = "用户管理", methods = "重置密码", serviceClass = SysUserService.class,
            queryMethod = "findByUsername", parameterType = "String", parameterKey = "username")
    public SysUser resetPassword(String username) {
        SysUser user = checkUser(username);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        return sysUserService.save(user);
    }

    /**
     * 修改密码
     *
     * @param username    用户名
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
    @PreAuthorize("authentication.principal.username.equals(#username)")
    public SysUser updatePassword(String username, String oldPassword, String newPassword) {
        if (StrUtil.hasBlank(oldPassword, newPassword, username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(username);
        if (user == null) {
            String error = String.format("根据用户名【%s】没有找到用户", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("密码错误");
            throw new RuntimeException("密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return sysUserService.save(user);
    }

    /**
     * 删除
     *
     * @param username 用户名
     */
    @DeleteMapping(value = "delete")
    @ApiOperation(value = "删除用户")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_DELETE')")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @SystemOperationLog(module = "用户管理", methods = "删除用户", serviceClass = SysUserService.class,
            queryMethod = "findByUsername", parameterType = "String", parameterKey = "username")
    public SysUser deleteUser(String username) {
        SysUser user = checkUser(username);
        approvalService.checkUserHaveApproval(user);
        user.setIsDelete("Y");
        return sysUserService.save(user);
    }

    /**
     * 设置用户角色
     *
     * @param username 用户名
     * @param roles 角色id集合，逗号分割
     */
    @PostMapping("updateRole")
    @ApiOperation(value = "设置用户角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "roles", value = "角色ID集合,逗号分割", required = true, dataType = "String"),
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_UPDATE_ROLE')")
    @SystemOperationLog(module = "用户管理", methods = "设置用户角色", serviceClass = SysUserService.class,
            queryMethod = "findByUsername", parameterType = "String", parameterKey = "username")
    public SysUser updateRole(String username, String roles) {
        SysUser user = checkUser(username);
        if (StrUtil.isNotBlank(roles)) {
            List<SysRole> roleList = sysRoleService.findByIdInAndIsDelete(Stream.of(roles.split(","))
                    .map(String::trim).map(Long::parseLong).collect(Collectors.toList()));
            user.setRoles(roleList);
        } else {
            user.setRoles(null);
        }
        return sysUserService.save(user);
    }

    //todo 是否用户编辑个人信息

    private SysUser checkUser(String username) {
        if (StrUtil.hasBlank(username)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = sysUserService.findByUsername(username);
        if (user == null) {
            String error = String.format("根据用户名【%s】没有找到用户", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        String isDelete  = "Y";
        if(isDelete.equals(user.getIsDelete())){
            String error = String.format("该用户【%s】已删除", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        return user;
    }
}
