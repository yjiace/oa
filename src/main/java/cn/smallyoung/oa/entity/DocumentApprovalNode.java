package cn.smallyoung.oa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @data 2020/11/19
 */
@Getter
@Setter
@Entity
@Table(name = "t_document_approval_node")
@EntityListeners({AuditingEntityListener.class})
public class DocumentApprovalNode {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 状态，NotStarted：未开始，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝
     */
    @Column(name = "status")
    private String status;

    /**
     * 需要审批的用户
     */
    @Column(name = "username")
    private String user;

    /**
     * 关联的审批
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "document_approval_id")
    private DocumentApproval documentApproval;

    /**
     * 从小到大排序
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 完成时间
     */
    @Column(name = "completed_time")
    @ApiModelProperty(notes = "完成时间")
    private LocalDateTime completedTime;

    /**
     * 修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @ApiModelProperty(notes = "修改时间")
    private LocalDateTime updateTime;

}