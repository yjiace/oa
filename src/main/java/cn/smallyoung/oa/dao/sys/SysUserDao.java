package cn.smallyoung.oa.dao.sys;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.sys.SysUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author smallyoung
 * @date 2020/10/26
 */

public interface SysUserDao extends BaseDao<SysUser, Long> {

    /**
     * 根据用户昵称查询用户
     *
     * @param username 用户名
     * @return 查询到的用户实体
     */
    SysUser findByUsername(String username);

    /**
     * 修改用户状态--启用、未启用
     *
     * @param id     用户id
     * @param status 用户状态标识
     * @return 修改成功条数
     */
    @Modifying
    @Query("update SysUser u set u.status=?2 where u.id=?1")
    Integer updateStatus(Long id, String status);

    /**
     * 修改用户删除标识
     *
     * @param id       用户id
     * @param isDelete 修改删除字段标识
     * @return 修改成功条数
     */
    @Modifying
    @Query("update SysUser u set u.isDelete=?2 where u.id=?1")
    Integer updateIsDelete(Long id, String isDelete);

    /**
     * 修改用户密码
     *
     * @param id       用户id
     * @param password 密码
     * @return 修改成功条数
     */
    @Modifying
    @Query("update SysUser u set u.password=?2 where u.id=?1")
    Integer updatePassword(Long id, String password);

}
