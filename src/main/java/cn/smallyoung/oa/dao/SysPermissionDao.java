package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.SysPermission;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
public interface SysPermissionDao extends BaseDao<SysPermission, Long> {

    /**
     * 修改用户状态
     *
     * @param id       权限id
     * @param isDelete 权限删除标识
     * @return 修改成功条数
     */
    @Modifying
    @Query("update SysPermission p set p.isDelete=?2 where p.id=?1")
    Integer updateStatus(Long id, String isDelete);

    /**
     * 查询用户在权限表中的所有权限
     *
     * @param ids 查询权限的id列表
     * @return 包含该id的所有权限列表
     */
    List<SysPermission> findByIdIn(List<Long> ids);
}
