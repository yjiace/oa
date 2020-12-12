package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.interfaces.DataName;
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
 * 文件审批
 * @author smallyoung
 * @data 2020/12/12
 */
@Getter
@Setter
@Entity
@ApiModel("文件审批")
@Table(name = "t_document_approval")
@EntityListeners({AuditingEntityListener.class})
public class DocumentApproval implements Serializable {

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
    @DataName(name = "发起人用户名")
    @Column(name = "initiator_username" )
    @ApiModelProperty(notes = "发起人用户名")
    private String initiatorUsername;

    @DataName(name = "承办单位")
    @Column(name = "organizer" )
    @ApiModelProperty(notes = "承办单位")
    private String organizer;

    /**
     * 呈批件HTML
     */
    @DataName(name = "呈批件HTML")
    @Column(name = "template_tml" )
    @ApiModelProperty(notes = "呈批件HTML")
    private String templateHtml;

    /**
     * 标题
     */
    @Column(name = "title")
    @DataName(name = "标题")
    @ApiModelProperty(notes = "标题")
    private String title;

    /**
     * 状态，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝
     */
    @DataName(name = "状态")
    @Column(name = "status")
    @ApiModelProperty(notes = "状态，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝")
    private String status;

    /**
     * 额外信息
     */
    @DataName(name = "额外信息")
    @Column(name = "extra")
    @ApiModelProperty(notes = "额外信息JSON格式")
    private String extra;

    /**
     * 当前审批节点（谁在审批）
     */
    @OneToOne
    @DataName(name = "当前审批节点")
    @JoinColumn(name = "node_id")
    @ApiModelProperty(notes = "当前审批节点")
    private DocumentApprovalNode node;

    /**
     * 关联的文件列表
     */
    @DataName(name = "关联的文件列表")
    @ApiModelProperty(notes = "关联的文件列表")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "t_document_approval_file",
            joinColumns = {@JoinColumn(name = "approval_id")}, inverseJoinColumns = {@JoinColumn(name = "file_id")})
    private List<AttachmentFile> attachmentFiles = new ArrayList<>();


    @DataName(name = "审批节点")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentApproval", fetch = FetchType.LAZY)
    @OrderBy(value = " sort ASC ")
    private List<DocumentApprovalNode> documentApprovalNodes = new ArrayList<>();

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
