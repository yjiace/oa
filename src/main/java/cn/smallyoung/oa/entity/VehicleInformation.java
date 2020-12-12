package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import cn.smallyoung.oa.interfaces.DataName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/11/18
 */

@Getter
@Setter
@Entity
@ApiModel("车辆管理")
@Table(name = "t_vehicle_information")
public class VehicleInformation extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 5419281866271341278L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 车牌号
     */
    @DataName(name = "车牌号")
    @Column(name = "plate_number" )
    @ApiModelProperty(notes = "车牌号")
    private String plateNumber;

    /**
     * 所属单位
     */
    @DataName(name = "所属单位")
    @Column(name = "company" )
    @ApiModelProperty(notes = "所属单位")
    private String company;

    /**
     * 车辆型号
     */
    @DataName(name = "车辆型号")
    @Column(name = "model" )
    @ApiModelProperty(notes = "车辆型号")
    private String model;

    /**
     * 状态：NotInUse：未使用，Approval：审批中，NotLeaving：未离场，NotReturned：未归还
     */
    @DataName(name = "状态")
    @Column(name = "status" )
    @ApiModelProperty(notes = "状态：NotInUse：未使用，Approval：审批中，NotLeaving：未离场，NotReturned：未归还")
    private String status;

    /**
     * 当前车辆审批审批
     */
    @OneToOne
    @JoinColumn(name = "current_car_record_id" )
    @ApiModelProperty(notes = "当前车辆审批审批")
    private VehicleApproval currentCarRecord;

}
