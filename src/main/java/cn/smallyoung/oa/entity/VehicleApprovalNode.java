package cn.smallyoung.oa.entity;

import cn.hutool.core.annotation.PropIgnore;
import cn.smallyoung.oa.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @data 2020/12/12
 */
@Getter
@Setter
@Entity
@ApiModel("车辆审批节点")
@Table(name = "t_vehicle_approval_node")
@EntityListeners({AuditingEntityListener.class})
public class VehicleApprovalNode implements Serializable {

    private static final long serialVersionUID = -7837696814649268664L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 状态，NotStarted：未开始，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝
     */
    @DataName(name = "状态")
    @Column(name = "status")
    @ApiModelProperty(notes = "状态，NotStarted：未开始，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝")
    private String status;

    /**
     * 需要审批的用户
     */
    @DataName(name = "需要审批的用户")
    @Column(name = "username")
    @ApiModelProperty(notes = "需要审批的用户")
    private String user;


    /**
     * 从小到大排序
     */
    @DataName(name = "审批顺序")
    @Column(name = "sort")
    @ApiModelProperty(notes = "审批顺序")
    private Integer sort;

    /**
     * 完成时间
     */
    @DataName(name = "完成时间")
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

    /**
     * 审批留言
     */
    @DataName(name = "审批留言")
    @Column(name = "message")
    private String message;

    /**
     * 关联的审批
     */
    @ManyToOne
    @PropIgnore
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @JoinColumn(name = "approval_id")
    private VehicleApproval vehicleApproval;

}
