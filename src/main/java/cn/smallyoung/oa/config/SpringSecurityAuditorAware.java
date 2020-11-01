package cn.smallyoung.oa.config;

import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author smallyoung
 * @data 2020/11/1
 */
@Slf4j
@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Resource
    private SysUserService sysUserService;

    @Override
    public Optional<String> getCurrentAuditor() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
        SysUser user = sysUserService.loadUserByUsername(authentication.getName());
        log.info("审计获取用户{}", user.getUsername());
        return Optional.of(user.getUsername());
    }

}
