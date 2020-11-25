package cn.smallyoung.oa.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/19
 */
@Getter
@Setter
@Entity
@ApiModel("文件审批")
@Table(name = "t_document_approval")
@EntityListeners({AuditingEntityListener.class})
public class DocumentApproval implements Serializable {

    private static final long serialVersionUID = -3598774125630968568L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发起人用户名
     */
    @Column(name = "initiator_username" )
    @ApiModelProperty(notes = "发起人用户名")
    private String initiatorUsername;

    /**
     * 状态，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝
     */
    @Column(name = "status")
    @ApiModelProperty(notes = "状态，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝")
    private String status;

    /**
     * 从小到大排序
     */
    @Column(name = "sort")
    @ApiModelProperty(notes = "内部排序字段")
    private Integer sort;

    /**
     * 当前审批节点（谁在审批）
     */
    @Column(name = "username")
    @ApiModelProperty(notes = "当前审批节点（谁在审批）")
    private String user;

    /**
     * 关联的文件列表
     */
    @ApiModelProperty(notes = "关联的文件列表")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "t_document_approval_file",
            joinColumns = {@JoinColumn(name = "document_approval")}, inverseJoinColumns = {@JoinColumn(name = "file_id")})
    private List<AttachmentFile> attachmentFiles = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentApproval", fetch = FetchType.LAZY)
    @OrderBy(value = " sort ASC ")
    private List<DocumentApprovalNode> documentApprovalNodes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentApproval", fetch = FetchType.LAZY)
    private List<DocumentApprovalComment> documentApprovalComments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentApproval", fetch = FetchType.LAZY)
    private List<DocumentApprovalLog> documentApprovalLogs = new ArrayList<>();

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @ApiModelProperty(notes = "修改时间")
    private LocalDateTime updateTime;
}
