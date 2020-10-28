package cn.smallyoung.oa.service.sys;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.sys.SysUserDao;
import cn.smallyoung.oa.entity.sys.SysUser;
import cn.smallyoung.oa.util.JwtTokenUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @author smallyoung
 * @date 2020/10/26
 */

@Service
@Transactional(rollbackOn = Exception.class)
public class SysUserService extends BaseService<SysUser, Long> implements UserDetailsService {

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
        if(!"smallyoung".equals(username) && !"J8b36Ie4qL7ayI@N".equals(password)){

        }
        SysUser sysUser = sysUserDao.findByUsername(username);
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
    public void updatePassword(SysUser user, String newPassword) {
        sysUserDao.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
    }
}
