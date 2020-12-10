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
 * @author smallyoung
 * @data 2020/11/19
 */
@Getter
@Setter
@Entity
@ApiModel("审批")
@Table(name = "t_approval")
@EntityListeners({AuditingEntityListener.class})
public class Approval implements Serializable {

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
    @DataName(name = "发起人用户名")
    @Column(name = "initiator_username" )
    @ApiModelProperty(notes = "发起人用户名")
    private String initiatorUsername;

    /**
     * 标题
     */
    @DataName(name = "标题")
    @Column(name = "title" )
    @ApiModelProperty(notes = "标题")
    private String title;

    /**
     * 类型，document：文档；vehicle：车辆
     */
    @DataName(name = "类型")
    @Column(name = "type" )
    @ApiModelProperty(notes = "类型，document：文档；vehicle：车辆")
    private String type;

    /**
     * 备注
     */
    @DataName(name = "备注")
    @Column(name = "remarks" )
    @ApiModelProperty(notes = "备注")
    private String remarks;

    /**
     * 编号
     */
    @DataName(name = "编号")
    @Column(name = "number" )
    @ApiModelProperty(notes = "编号")
    private String number;

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
    @ApiModelProperty(notes = "额外信息，JSON格式")
    private String extra;

    /**
     * 申请用车ID
     */
    @DataName(name = "申请用车ID")
    @Column(name = "vehicle_id")
    @ApiModelProperty(notes = "申请用车ID")
    private Long vehicleId;

    /**
     * 目的地
     */
    @DataName(name = "目的地")
    @Column(name = "destination")
    @ApiModelProperty(notes = "目的地")
    private String  destination;

    /**
     * 从小到大排序
     */
    @Column(name = "sort")
    @ApiModelProperty(notes = "内部排序字段")
    private Integer sort;

    /**
     * 当前审批节点（谁在审批）
     */
    @OneToOne
    @DataName(name = "当前审批节点")
    @JoinColumn(name = "node_id")
    @ApiModelProperty(notes = "当前审批节点")
    private ApprovalNode node;

    /**
     * 关联的文件列表
     */
    @DataName(name = "关联的文件列表")
    @ApiModelProperty(notes = "关联的文件列表")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "t_approval_file",
            joinColumns = {@JoinColumn(name = "approval_id")}, inverseJoinColumns = {@JoinColumn(name = "file_id")})
    private List<AttachmentFile> attachmentFiles = new ArrayList<>();


    @DataName(name = "审批节点")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "approval", fetch = FetchType.LAZY)
    @OrderBy(value = " sort ASC ")
    private List<ApprovalNode> approvalNodes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "approval", fetch = FetchType.LAZY)
    private List<ApprovalComment> approvalComments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "approval", fetch = FetchType.LAZY)
    private List<ApprovalLog> approvalLogs = new ArrayList<>();

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

    /**
     * 关联的用车记录
     */
    @Transient
    @ApiModelProperty(notes = "关联的用车记录")
    private CarRecord carRecord;
}
