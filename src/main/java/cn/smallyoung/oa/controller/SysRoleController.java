package cn.smallyoung.oa.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.SysPermission;
import cn.smallyoung.oa.entity.SysRole;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.SysPermissionService;
import cn.smallyoung.oa.service.SysRoleService;
import cn.smallyoung.oa.vo.SysRoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/10/31
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/sys/role")
@Api(tags = "角色管理")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 查询所有权限
     */
    @GetMapping(value = "findAllPermission")
    @ApiOperation(value = "查询所有权限")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_SAVE')")
    public List<Tree<String>> findAllPermission(HttpServletRequest request) {
        List<SysPermission> sysPermissions = sysPermissionService.findAll(WebUtils.getParametersStartingWith(request, "search_"));
        return TreeUtil.build(sysPermissions, "0", new TreeNodeConfig(),
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setParentId(treeNode.getParentId().toString());
                    tree.setName(treeNode.getName());
                    tree.putExtra("val", treeNode.getVal());
                });
    }

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @ApiOperation(value = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_FIND')")
    public Page<SysRole> findAll(@RequestParam(defaultValue = "1") Integer page,
                                 HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return sysRoleService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 新增角色
     */
    @PostMapping(value = "save")
    @ApiOperation(value = "新增角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "角色名称", dataType = "String"),
            @ApiImplicitParam(name = "comments", value = "角色备注", dataType = "String"),
            @ApiImplicitParam(name = "permissions", value = "权限ID列表", dataType = "List")
    })
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_SAVE')")
    @SystemOperationLog(module = "角色管理", methods = "编辑角色", serviceClass = SysRoleService.class, queryMethod = "findOne",
            parameterType = "Long", parameterKey = "role.id")
    public SysRole save(SysRoleVO roleVO) {
        if (roleVO == null || StrUtil.hasBlank(roleVO.getName())) {
            throw new NullPointerException("参数错误");
        }
        roleVO.setId(null);
        SysRole role = new SysRole();
        BeanUtil.copyProperties(roleVO, role, CopyOptions.create().setIgnoreNullValue(true));
        if (CollUtil.isNotEmpty(roleVO.getPermissions())) {
            role.setSysPermissions(sysPermissionService.findByIdInAndIsDelete(roleVO.getPermissions()));
        }
        role.setIsDelete("N");
        return sysRoleService.save(role);
    }

    /**
     * 编辑角色
     */
    @PostMapping(value = "update")
    @ApiOperation(value = "编辑角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "角色名称", dataType = "String"),
            @ApiImplicitParam(name = "comments", value = "角色备注", dataType = "String"),
            @ApiImplicitParam(name = "permissions", value = "权限ID列表", dataType = "List")
    })
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_SAVE')")
    @SystemOperationLog(module = "角色管理", methods = "编辑角色", serviceClass = SysRoleService.class, queryMethod = "findOne",
            parameterType = "Long", parameterKey = "role.id")
    public SysRole update(SysRoleVO roleVO) {
        if (roleVO == null || roleVO.getId() == null || StrUtil.hasBlank(roleVO.getName())) {
            throw new NullPointerException("参数错误");
        }
        SysRole role = sysRoleService.findOne(roleVO.getId());
        if (role == null) {
            String error = String.format("根据ID【%s】没有找到角色", roleVO.getId());
            log.error(error);
            throw new RuntimeException(error);
        }
        BeanUtil.copyProperties(roleVO, role, CopyOptions.create().setIgnoreNullValue(true));
        if (CollUtil.isNotEmpty(roleVO.getPermissions())) {
            role.setSysPermissions(sysPermissionService.findByIdInAndIsDelete(roleVO.getPermissions()));
        }
        return sysRoleService.save(role);
    }

    /**
     * 删除角色
     *
     * @param id 需要删除的角色ID
     * @return ResultMap封装好的返回数据
     */
    @DeleteMapping(value = "delete")
    @ApiOperation(value = "删除角色")
    @PreAuthorize("hasRole('ROLE_ROLE') or hasRole('ROLE_ROLE_DELETE')")
    @ApiImplicitParam(name = "id", value = "需要删除的角色ID", required = true, dataType = "Long")
    @SystemOperationLog(module = "角色管理", methods = "删除角色", serviceClass = SysRoleService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public SysRole delete(Long id) {
        SysRole role = checkRole(id);
        role.setIsDelete("Y");
        return sysRoleService.save(role);
    }

    private SysRole checkRole(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        SysRole role = sysRoleService.findOne(id);
        if (role == null) {
            String error = String.format("根据ID【%s】没有找到该角色", id);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        String isDelete = "Y";
        if (isDelete.equals(role.getIsDelete())) {
            String error = String.format("该角色【%s】已删除", id);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        return role;
    }

}
