package cn.smallyoung.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.sys.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Slf4j
@ResponseResultBody
@RequestMapping("/login")
public class LoginController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    @GetMapping
    public String login(String username, String password) {
        if (StrUtil.hasBlank(username, password)) {
            throw new NullPointerException("参数错误");
        }
        return sysUserService.login(username, password);
    }
}
