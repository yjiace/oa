package cn.smallyoung.oa.service;

import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.entity.SysPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Service
@Transactional(readOnly = true)
public class SysPermissionService extends BaseService<SysPermission, Long> {

}
