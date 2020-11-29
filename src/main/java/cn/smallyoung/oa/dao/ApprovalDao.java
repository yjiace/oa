package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.Approval;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/20
 */
public interface ApprovalDao extends BaseDao<Approval, Long> {

    Approval findByNumber(String number);

    @Query(value = "SELECT count(a.id) FROM t_approval a where a.initiator_username = ?1 and a.status = 'Approval' ", nativeQuery = true)
    long checkUserHaveApproval(String username);

    @Query(value = "SELECT count(a.id) FROM t_approval a INNER JOIN t_approval_node n on n.approval_id = a.id where n.username = ?1 and n.status = 'Approval' ", nativeQuery = true)
    long countApprovalRequired(String username);

    @Query(value = "SELECT a.* FROM t_approval a INNER JOIN t_approval_node n on n.approval_id = a.id where n.username = ?1 and n.status = 'Approval' ORDER BY a.sort limit ?2 offset ?3 ", nativeQuery = true)
    List<Approval> findAllApprovalRequired(String username, Integer page, Integer limit);
}