package cn.smallyoung.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.sys.SysRole;
import cn.smallyoung.oa.entity.sys.SysUser;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.sys.SysRoleService;
import cn.smallyoung.oa.service.sys.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@ResponseResultBody
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private SysRoleService sysRoleService;

    /**
     * 获取用户列表
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    public Page<SysUser> findAll(@RequestParam(defaultValue = "1") Integer page, javax.servlet.http.HttpServletRequest request,
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
    public void save(SysUser user, Long[] roleIds) {
        if (user.getId() == null) {
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus("Y");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setIsDelete("N");
        } else {
            SysUser oldUser = sysUserService.findOne(user.getId());
            user.setCreateTime(oldUser.getCreateTime());
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus(oldUser.getStatus());
            user.setPassword(oldUser.getPassword());
            user.setRole(oldUser.getRole());
            user.setIsDelete(oldUser.getIsDelete());
        }
        if (roleIds != null) {
            List<SysRole> roleList = sysRoleService.findByIdInAndIsDelete(Arrays.asList(roleIds));
            user.setRole(roleList);
        }else{
            user.setRole(null);
        }
        sysUserService.save(user);
    }

    /**
     * 更改用户状态
     *
     * @param id     需要更改状态的用户ID
     * @param status 需要更改用户的状态：Y，启用；N，禁用
     */
    @PostMapping(value = "updateStatus/{id}")
    public void updateStatus(@PathVariable("id") Long id, String status) {
        if (id == null || StrUtil.hasBlank(status)) {
            throw new NullPointerException("参数错误");
        }
        sysUserService.updateStatus(id, status);
    }

    /**
     * 重置密码
     *
     * @param id       需要重置密码的用户ID
     * @param password 需要重置的密码
     */
    @PostMapping(value = "resetPassword/{id}")
    public void resetPassword(@PathVariable("id") Long id, String password) {
        if (id == null || StrUtil.hasBlank(password)) {
            throw new NullPointerException("参数错误");
        }
        sysUserService.resetPassword(id, password);
    }

    /**
     * 修改密码
     *
     * @param id          需要修改密码的用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @PostMapping(value = "updatePassword/{id}")
    public void updatePassword(@PathVariable("id") Long id, String oldPassword, String newPassword) {
        if (id == null || StrUtil.hasBlank(oldPassword, newPassword)) {
            throw new NullPointerException("参数错误");
        }
        SysUser user = new SysUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UsernameNotFoundException(String.format("Wrong password with password '%s'.", newPassword));
        }
        sysUserService.updatePassword(user, newPassword);
    }

    /**
     * 删除
     *
     * @param id 需要删除的用户ID
     */
    @PostMapping(value = "delete/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        sysUserService.updateIsDelete(id, "N");
    }
}
