package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.SysUserDao;
import cn.smallyoung.oa.entity.SysRole;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@Service
@Transactional(rollbackOn = Exception.class)
public class SysUserService extends BaseService<SysUser, Long> implements UserDetailsService {

    @Value("${default.user.password}")
    private String defaultPassword;
    @Value("${default.user.username}")
    private String defaultUserName;

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        return user;
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    public String login(String username, String password) {
        SysUser sysUser;
        if(username.equals(defaultUserName) && password.equals(defaultPassword)){
            SysRole role = new SysRole();
            role.setName("admin");
            role.setPermissions(new ArrayList<>());
            sysUser = new SysUser();
            sysUser.setRole(Collections.singletonList(role));
            log.info("通过系统配置账户登录");
        }else{
            sysUser = sysUserDao.findByUsername(username);
            if (sysUser == null) {
                throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
            }
            if (!passwordEncoder.matches(password, sysUser.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(sysUser);
    }


    /**
     * 根据ID修改用户状态
     *
     * @param id     用户ID
     * @param status 用户状态标识
     * @return 修改成功条数
     */
    public Integer updateStatus(Long id, String status) {
        return sysUserDao.updateStatus(id, status);
    }

    /**
     * 根据ID删除用户--修改删除标识
     *
     * @param id       用户ID
     * @param isDelete 修改删除字段标识
     * @return 修改成功条数
     */
    public Integer updateIsDelete(Long id, String isDelete) {
        return sysUserDao.updateIsDelete(id, isDelete);
    }

    /**
     * 重置密码
     *
     * @param id       用户ID
     * @param password 密码
     * @return 修改成功条数
     */
    public Integer resetPassword(Long id, String password) {
        return sysUserDao.updatePassword(id, passwordEncoder.encode(password));
    }

    /**
     * 修改密码
     *
     * @param user        登录用户
     * @param newPassword 新密码
     */
    public Integer updatePassword(SysUser user, String newPassword) {
        return sysUserDao.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
    }
}
