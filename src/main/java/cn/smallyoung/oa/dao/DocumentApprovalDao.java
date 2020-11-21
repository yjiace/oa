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

    @Query(value = "SELECT count(a.id) FROM t_document_approval a INNER JOIN t_document_approval_node n on n.username = ?1 ", nativeQuery = true)
    long countApprovalRequired(String username);

    @Query(value = "SELECT a.* FROM t_document_approval a INNER JOIN t_document_approval_node n on n.username = ?1 ORDER BY a.sort ", nativeQuery = true)
    List<DocumentApproval> findAllApprovalRequired(String username);
}
