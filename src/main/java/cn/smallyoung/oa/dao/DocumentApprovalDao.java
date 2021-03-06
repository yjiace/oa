package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.DocumentApproval;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/20
 */
public interface DocumentApprovalDao extends BaseDao<DocumentApproval, Long> {

    @Query(value = "SELECT count(a.id) FROM t_document_approval a where a.initiator_username = ?1 and a.status = 'Approval' ", nativeQuery = true)
    long checkUserHaveApproval(String username);

    @Query(value = "SELECT count(a.id) FROM t_document_approval a INNER JOIN t_document_approval_node n on n.approval_id = a.id " +
            "where n.username = ?1 and n.status = 'Approval' ", nativeQuery = true)
    long countApprovalRequired(String username);

    @Query(value = "SELECT a.* FROM t_document_approval a INNER JOIN t_document_approval_node n on n.approval_id = a.id" +
            " where n.username = ?1 and n.status = 'Approval' ORDER BY a.create_time limit ?2 offset ?3 ", nativeQuery = true)
    List<DocumentApproval> findAllApprovalRequired(String username, Integer page, Integer limit);
}
