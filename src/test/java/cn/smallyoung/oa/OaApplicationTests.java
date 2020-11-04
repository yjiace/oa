package cn.smallyoung.oa;

import cn.smallyoung.oa.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest(classes = OaApplication.class)
class OaApplicationTests {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testPassword() {
        System.out.println(passwordEncoder.encode("smallyoung"));
    }

    @Test
    public void testLogin(){
        System.out.println(sysUserService.login("smallyoung", "smallyoung"));
    }


}
