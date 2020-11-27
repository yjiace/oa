package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.SysPermissionDao;
import cn.smallyoung.oa.entity.SysPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Service
@Transactional(readOnly = true)
public class SysPermissionService extends BaseService<SysPermission, Long> {

    @Resource
    private SysPermissionDao sysPermissionDao;

    /**
     * 根据id集合查询所有权限
     *
     * @param idList 权限id列表
     * @return 权限对象列表
     */
    public List<SysPermission> findByIdInAndIsDelete(List<Long> idList) {
        return sysPermissionDao.findByIdInAndIsDelete(idList, "N");
    }

}
