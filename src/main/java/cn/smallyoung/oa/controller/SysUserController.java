package cn.smallyoung.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.sys.SysUser;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.sys.SysUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@Controller
@ResponseResultBody
@RequestMapping("/sys/user")
@Api(tags = "后台用户管理")
public class SysUserController {

    @Value("${default.password}")
    private String defaultPassword;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 获取用户列表
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @ApiOperation(value = "获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    public Page<SysUser> findAll(@RequestParam(defaultValue = "1") Integer page, HttpServletRequest request,
                                 @RequestParam(defaultValue = "9") Integer limit) {
        return sysUserService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 保存用户或者更新用户
     *
     * @param user 需要保存或修改的用户实体
     */
    @PostMapping(value = "save")
    @ApiOperation(value = "保存用户")
    @ApiOperationSupport(ignoreParameters = {"role", "password", "creator", "createTime", "updater", "updateTime", "authorities"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long"),
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态，Y正常，N冻结", dataType = "String"),
            @ApiImplicitParam(name = "isDelete", value = "删除标识：N正常，Y删除", dataType = "String"),
    })
    public SysUser save(SysUser user) {
        if (user.getId() == null) {
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus("Y");
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setIsDelete("N");
        } else {
            SysUser oldUser = sysUserService.findOne(user.getId());
            user.setCreateTime(oldUser.getCreateTime());
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus(oldUser.getStatus());
            user.setPassword(oldUser.getPassword());
            user.setRole(oldUser.getRole());
            user.setIsDelete(oldUser.getIsDelete());
            user.setRole(oldUser.getRole());
        }
        return sysUserService.save(user);
    }

    /**
     * 更改用户状态
     *
     * @param id     需要更改状态的用户ID
     * @param status 需要更改用户的状态：Y，启用；N，禁用
     */
    @PostMapping(value = "updateStatus")
    @ApiOperation(value = "修改用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "状态：Y，启用；N，禁用", required = true, dataType = "String")
    })
    public Integer updateStatus(Long id, String status) {
        if (id == null || StrUtil.hasBlank(status)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.updateStatus(id, status);
    }

    /**
     * 重置密码
     *
     * @param id       需要重置密码的用户ID
     */
    @PostMapping(value = "resetPassword")
    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    public Integer resetPassword(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.resetPassword(id, defaultPassword);
    }

    /**
     * 修改密码
     *
     * @param id          需要修改密码的用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @PostMapping(value = "updatePassword")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    public Integer updatePassword(Long id, String oldPassword, String newPassword) {
        if (id == null || StrUtil.hasBlank(oldPassword, newPassword)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = new SysUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UsernameNotFoundException(String.format("Wrong password with password '%s'.", newPassword));
        }
        return sysUserService.updatePassword(user, newPassword);
    }

    /**
     * 删除
     *
     * @param id 需要删除的用户ID
     */
    @DeleteMapping(value = "delete")
    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    public Integer deleteUser(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.updateIsDelete(id, "N");
    }
}
