package cn.smallyoung.oa.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.SysUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@RestController
@ResponseResultBody
@Api(tags = "登录模块")
public class LoginController {

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Resource
    private SysUserService sysUserService;

    private static final String CHAPTER_KEY = "login-captcha";

    @GetMapping("/loginByTest")
    @ApiOperation(value = "测试，返回登录token")
    public String loginByTest(){
        String token = sysUserService.login("smallyoung", "smallyoung");
        return tokenHead + " " + token;
    }
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    @GetMapping("/login")
    @ApiOperation(value = "登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, dataType = "String")
    })
    public Dict login(String username, String password, String captcha, HttpSession session) {
        if (StrUtil.hasBlank(username, password, captcha)) {
            throw new NullPointerException("参数错误");
        }
        Object obj = session.getAttribute(CHAPTER_KEY);
        if(!(obj instanceof String)){
            throw new NullPointerException("验证码错误");
        }
        session.removeAttribute(CHAPTER_KEY);
        if(!captcha.equalsIgnoreCase(obj.toString())){
            throw new SessionAuthenticationException("验证码错误");
        }
        String token = sysUserService.login(username, password);
        return Dict.create().set("tokenHead", tokenHead).set("token", token);
    }

    @GetMapping("/captcha")
    @ApiOperation(value = "获取验证码")
    public void captcha(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        session.setAttribute(CHAPTER_KEY, captcha.getCode());
        OutputStream os = response.getOutputStream();
        captcha.write(os);
        os.flush();
        os.close();
    }
}
