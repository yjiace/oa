package cn.smallyoung.oa.component;

import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.service.SysUserService;
import cn.smallyoung.oa.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 *
 * @author smallyoung
 * @data 2020/10/27
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,  FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            log.info("checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SysUser user = sysUserService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken, user)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("authenticated user:{}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    response.setHeader("userId", user.getUsername());
                    if(jwtTokenUtil.canRefresh(authToken)){
                        response.setHeader(tokenHeader, jwtTokenUtil.refreshToken(authToken));
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

}
