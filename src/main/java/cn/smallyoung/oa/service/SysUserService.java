package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.SysUserDao;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j

@Service
@Transactional(readOnly = true)
public class SysUserService extends BaseService<SysUser, String> implements UserDetailsService {

    @Value("${default.password}")
    private String defaultPassword;
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserDao.findEffectiveByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        user.getAuthorities();
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public SysUser save(String username,String name, String phone, String mobile){
        SysUser user = sysUserDao.findById(username).orElse(null);
        if(user == null){
            user = new SysUser();
            user.setUsername(username);
            user.setStatus("Y");
            user.setIsDelete("N");
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }
        user.setName(name);
        user.setPhone(phone);
        user.setMobile(mobile);
        return sysUserDao.save(user);
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    public String login(String username, String password) {
        SysUser sysUser = sysUserDao.findEffectiveByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        if (!passwordEncoder.matches(password, sysUser.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(sysUser);
    }


    /**
     * 根据ID修改用户状态
     *
     * @param username 用户名
     * @param status   用户状态标识
     * @return 修改成功条数
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatus(String username, String status) {
        return sysUserDao.updateStatus(username, status);
    }

    /**
     * 根据ID删除用户--修改删除标识
     *
     * @param username 用户名
     * @param isDelete 修改删除字段标识
     * @return 修改成功条数
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateIsDelete(String username, String isDelete) {
        return sysUserDao.updateIsDelete(username, isDelete);
    }

    /**
     * 重置密码
     *
     * @param username 用户名
     * @return 修改成功条数
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer resetPassword(String username) {
        return sysUserDao.updatePassword(username, passwordEncoder.encode(defaultPassword));
    }

    /**
     * 修改密码
     *
     * @param user        登录用户
     * @param newPassword 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updatePassword(SysUser user, String newPassword) {
        return sysUserDao.updatePassword(user.getUsername(), passwordEncoder.encode(newPassword));
    }
}
