package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.SysUserDao;
import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
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
public class SysUserService extends BaseService<SysUser, Long> implements UserDetailsService {

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public SysUser findOne(Long id){
        return super.findOne(id);
    }

    public SysUser findByUsername(String username){
        return sysUserDao.findByUsername(username);
    }

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserDao.findEffectiveByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        user.getAuthorities();
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
        SysUser sysUser = loadUserByUsername(username);
        if (!passwordEncoder.matches(password, sysUser.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(sysUser);
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
