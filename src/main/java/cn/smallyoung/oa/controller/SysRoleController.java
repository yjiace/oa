package cn.smallyoung.oa.controller;

import cn.smallyoung.oa.entity.SysRole;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author smallyoung
 * @data 2020/10/31
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/role")
@Api(tags = "角色模块")
public class SysRoleController extends BaseController<SysRole, Long> {

}
