package cn.smallyoung.oa.service.sys;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.sys.SysRoleDao;
import cn.smallyoung.oa.entity.sys.SysRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class SysRoleService extends BaseService<SysRole, Long> {

    @Resource
    private SysRoleDao sysRoleDao;

    /**
     * 根据ID修改角色状态
     *
     * @param id       需要修改的角色id
     * @param isDelete 修改的删除字段标识
     * @return 修改成功条数
     */
    public Integer updateStatus(Long id, String isDelete) {
        return sysRoleDao.updateStatus(id, isDelete);
    }

    /**
     * 根据id集合查询所有角色
     *
     * @param idList 角色id列表
     * @return 角色对象列表
     */
    public List<SysRole> findByIdInAndIsDelete(List<Long> idList) {
        return sysRoleDao.findByIdInAndIsDelete(idList, "N");
    }
}
