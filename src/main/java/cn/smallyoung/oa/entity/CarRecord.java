package cn.smallyoung.oa.entity;

/**
 * @author smallyoung
 * @data 2020/11/18
 */

import cn.hutool.core.annotation.PropIgnore;
import cn.smallyoung.oa.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ApiModel("用车申请记录")
@Table(name = "t_car_record")
@EntityListeners({AuditingEntityListener.class})
public class CarRecord implements Serializable {

    private static final long serialVersionUID = 8917972524655762908L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 车辆信息
     */
    @JsonIgnore
    @PropIgnore
    @DataName(name = "车辆信息")
    @JoinColumn(name = "vehicle_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ApiModelProperty(notes = "车辆信息")
    private VehicleInformation vehicleInformation;

    /**
     * 状态，NotInUse：未使用，NotLeaving：未离场，NotReturned：未归还
     */
    @Column(name = "status" )
    @DataName(name = "状态")
    @ApiModelProperty(notes = "状态，NotInUse：未使用，NotLeaving：未离场，NotReturned：未归还")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carRecord", fetch = FetchType.LAZY)
    private List<CarRecordLog> carRecordLogs = new ArrayList<>();

    /**
     * 操作人
     */
    @CreatedBy
    @Column(name = "username" )
    @ApiModelProperty(notes = "操作人")
    private String username;

    /**
     * 目的地
     */
    @DataName(name = "目的地")
    @Column(name = "destination" )
    @ApiModelProperty(notes = "目的地")
    private String  destination;

    /**
     * 备注
     */
    @DataName(name = "备注")
    @Column(name = "remarks" )
    @ApiModelProperty(notes = "备注")
    private String remarks;

    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private LocalDateTime createTime;
}
