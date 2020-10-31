package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.SysPermissionDao;
import cn.smallyoung.oa.entity.SysPermission;
import cn.smallyoung.oa.entity.SysRole;
import cn.smallyoung.oa.entity.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class SysPermissionService extends BaseService<SysPermission, Long> {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysPermissionDao sysPermissionDao;

    /**
     * 获取用户菜单
     *
     * @param user 系统用户
     * @return 该系统用户所拥有的权限列表
     */
    public List<SysPermission> getMenusByUser(SysUser user) {
        return null;
    }


    /**
     * 根据ID修改权限状态
     *
     * @param id       权限id
     * @param isDelete 权限删除标识
     * @return 修改成功条数
     */
    public Integer updateStatus(Long id, String isDelete) {
        return sysPermissionDao.updateStatus(id, isDelete);
    }

    /**
     * 角色权限管理---保存
     *
     * @param roleId  角色id
     * @param permIds 权限列表
     * @return 是否成功
     */
    public boolean updateRolePermission(Long roleId, Long[] permIds) {
        Optional<SysRole> roleOptional = sysRoleService.findById(roleId);
        if(roleOptional.isPresent()){
            SysRole role = roleOptional.get();
            role.setPermissions(this.getPermissionListByIds(permIds));
            sysRoleService.save(role);
            return true;
        }
        return false;
    }

    /**
     * 根据权限id列表查询
     *
     * @param permIds 权限id集合
     * @return 权限集合
     */
    private List<SysPermission> getPermissionListByIds(Long[] permIds) {
        return sysPermissionDao.findByIdIn(Arrays.asList(permIds));
    }
}
