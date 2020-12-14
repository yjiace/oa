package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.interfaces.DataName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/12/12
 */
@Getter
@Setter
@Entity
@ApiModel("车辆审批")
@Table(name = "t_vehicle_approval")
@EntityListeners({AuditingEntityListener.class})
public class VehicleApproval implements Serializable {

    private static final long serialVersionUID = -4389105416338012221L;
    /**
    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DataName(name = "车辆审批时间")
    @Column(name = "application_time" )
    @ApiModelProperty(notes = "车辆审批时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applicationTime;
    /**
     * 发起人用户名
     */
    @DataName(name = "发起人用户名")
    @Column(name = "initiator_username" )
    @ApiModelProperty(notes = "发起人用户名")
    private String initiatorUsername;

    /**
     * 用车单位
     */
    @DataName(name = "用车单位")
    @Column(name = "transport_unit")
    @ApiModelProperty(notes = "用车单位")
    private String transportUnit;

    /**
     * 车号
     */
    @DataName(name = "车号")
    @Column(name = "vehicle_number")
    @ApiModelProperty(notes = "车号")
    private String vehicleNumber;

    /**
     * 车辆型号
     */
    @DataName(name = "车辆型号")
    @Column(name = "model")
    @ApiModelProperty(notes = "车辆型号")
    private String model;

    /**
     * 乘车地点
     */
    @DataName(name = "乘车地点")
    @Column(name = "pick_up_location")
    @ApiModelProperty(notes = "乘车地点")
    private String pickUpLocation;

    /**
     * 出车事由
     */
    @DataName(name = "出车事由")
    @Column(name = "cause_of_car")
    @ApiModelProperty(notes = "出车事由")
    private String causeOfCar;

    /**
     * 行驶路线
     */
    @DataName(name = "行驶路线")
    @Column(name = "driving_route")
    @ApiModelProperty(notes = "行驶路线")
    private String drivingRoute;

    /**
     * 出场时间
     */
    @DataName(name = "出场时间")
    @Column(name = "playing_time")
    @ApiModelProperty(notes = "出场时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playingTime;

    /**
     * 出库表底
     */
    @DataName(name = "出库表底")
    @Column(name = "outbound_table_bottom")
    @ApiModelProperty(notes = "出库表底")
    private String outboundTableBottom;

    /**
     * 行驶公里
     */
    @DataName(name = "行驶公里")
    @Column(name = "kilometers_traveled")
    @ApiModelProperty(notes = "行驶公里")
    private String kilometersTraveled;

    /**
     * 部门领导
     */
    @DataName(name = "部门领导")
    @Column(name = "department_heads")
    @ApiModelProperty(notes = "部门领导")
    private String departmentHeads;

    /**
     * 带车干部
     */
    @DataName(name = "带车干部")
    @Column(name = "car_cadre")
    @ApiModelProperty(notes = "带车干部")
    private String carCadre;

    /**
     * 联系电话
     */
    @DataName(name = "联系电话")
    @Column(name = "phone")
    @ApiModelProperty(notes = "联系电话")
    private String phone;

    /**
     * 驾驶员姓名
     */
    @DataName(name = "驾驶员姓名")
    @Column(name = "driver_name")
    @ApiModelProperty(notes = "驾驶员姓名")
    private String driverName;

    /**
     * 承运员额
     */
    @DataName(name = "承运员额")
    @Column(name = "carriage_amount")
    @ApiModelProperty(notes = "承运员额")
    private String carriageAmount;

    /**
     * 承运物品
     */
    @DataName(name = "承运物品")
    @Column(name = "carriage_items")
    @ApiModelProperty(notes = "承运物品")
    private String carriageItems;

    /**
     * 用车台次
     */
    @DataName(name = "用车台次")
    @Column(name = "user_car_number")
    @ApiModelProperty(notes = "用车台次")
    private Integer userCarNumber;

    /**
     * 回场时间
     */
    @DataName(name = "回场时间")
    @Column(name = "return_time")
    @ApiModelProperty(notes = "回场时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime;

    /**
     * 回库表底
     */
    @DataName(name = "回库表底")
    @Column(name = "return_table_bottom")
    @ApiModelProperty(notes = "回库表底")
    private String returnTableBottom;

    /**
     * 乘车人签字
     */
    @DataName(name = "乘车人签字")
    @Column(name = "passenger_signs")
    @ApiModelProperty(notes = "乘车人签字")
    private String passengerSigns;

    /**
     * 派车人
     */
    @DataName(name = "派车人")
    @Column(name = "car_dispatcher")
    @ApiModelProperty(notes = "派车人")
    private String carDispatcher;

    /**
     * 批准首长
     */
    @DataName(name = "批准首长")
    @Column(name = "approved_chief")
    @ApiModelProperty(notes = "批准首长")
    private String approvedChief;

    /**
     * 备注
     */
    @DataName(name = "备注")
    @Column(name = "remarks")
    @ApiModelProperty(notes = "备注")
    private String remarks;


    /**
     * 当前审批节点（谁在审批）
     */
    @OneToOne
    @DataName(name = "当前审批节点")
    @JoinColumn(name = "node_id")
    @ApiModelProperty(notes = "当前审批节点")
    private VehicleApprovalNode node;

    /**
     * 审批节点
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicleApproval", fetch = FetchType.LAZY)
    @OrderBy(value = " sort ASC ")
    private List<VehicleApprovalNode> vehicleApprovalNodes = new ArrayList<>();

    /**
     * 状态，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝
     */
    @DataName(name = "状态")
    @Column(name = "status")
    @ApiModelProperty(notes = "状态，Approval：审批中，Completed：已完成，Withdrawn：已撤回，Rejected：已拒绝")
    private String status;

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
     * 额外信息
     */
    @DataName(name = "额外信息")
    @Column(name = "extra")
    @ApiModelProperty(notes = "额外信息，JSON格式")
    private String extra;

    @Transient
    private List<String> username;
}
