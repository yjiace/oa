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
public class SysUserService extends BaseService<SysUser, String> implements UserDetailsService {

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    public SysUser findByUsername(String username) {
        return sysUserDao.findByUsername(username);
    }

    @Override
    public SysUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserDao.findEffectiveByUsername(username);
        if (user == null) {
            String error = String.format("根据用户名【%s】没有找到用户", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
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
            log.error("用户【{}】登录系统，密码【{}】错误", username, password);
            throw new BadCredentialsException("密码不正确");
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(sysUser);
    }

    public String currentlyLoggedInUser(){
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
