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

}
