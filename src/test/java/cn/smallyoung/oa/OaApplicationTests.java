package cn.smallyoung.oa;

import cn.smallyoung.oa.entity.SysUser;
import cn.smallyoung.oa.service.MessageNotificationService;
import cn.smallyoung.oa.service.SysUserService;
import cn.smallyoung.oa.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest(classes = OaApplication.class)
class OaApplicationTests {

    @Resource
    private JwtTokenUtil JwtTokenUtil;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private MessageNotificationService messageNotificationService;

    @Test
    public void testPassword() {
        System.out.println(passwordEncoder.encode("smallyoung"));
    }

    @Test
    public void testLogin(){
        System.out.println(sysUserService.login("smallyoung", "smallyoung"));
    }

    @Test
    public void testJwt(){
        SysUser sysuser = sysUserService.findByUsername("yjc");
        String token = JwtTokenUtil.generateToken(sysuser);
        System.out.println(token);
        System.out.println(JwtTokenUtil.canRefresh(token));
        System.out.println(JwtTokenUtil.refreshToken(token));
    }

    @Test
    public void testReleaseMessage(){
        messageNotificationService.releaseMessage("smallyoung", "test","test");
    }
}
