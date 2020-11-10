package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.SysUser;
import org.springframework.data.jpa.repository.Query;

/**
 * @author smallyoung
 * @date 2020/10/26
 */

public interface SysUserDao extends BaseDao<SysUser, String> {

    /**
     * 根据用户昵称查询用户
     *
     * @param username 用户名
     * @return 查询到的用户实体
     */
    @Query("select u from SysUser u where u.username = ?1 and u.status = 'Y' and u.isDelete = 'N' ")
    SysUser findEffectiveByUsername(String username);

    /**
     * 根据用户昵称查询用户
     *
     * @param username 用户名
     * @return 查询到的用户实体
     */
    SysUser findByUsername(String username);

}
