package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.VehicleApproval;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author yangn
 */
public interface VehicleApprovalDao extends BaseDao<VehicleApproval, Long> {

    @Query(value = "SELECT count(a.id) FROM t_vehicle_approval a where a.initiator_username = ?1 and a.status = 'Approval' ", nativeQuery = true)
    long checkUserHaveApproval(String username);

    @Query(value = "SELECT count(a.id) FROM t_vehicle_approval a INNER JOIN t_vehicle_approval_node n on n.approval_id = a.id " +
            "where n.username = ?1 and n.status = 'Approval' ", nativeQuery = true)
    long countApprovalRequired(String username);

    @Query(value = "SELECT a.* FROM t_vehicle_approval a INNER JOIN t_vehicle_approval_node n on n.approval_id = a.id" +
            " where n.username = ?1 and n.status = 'Approval' ORDER BY a.sort limit ?2 offset ?3 ", nativeQuery = true)
    List<VehicleApproval> findAllApprovalRequired(String username, Integer page, Integer limit);
    
}
