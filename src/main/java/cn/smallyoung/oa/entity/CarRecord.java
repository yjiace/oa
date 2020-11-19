package cn.smallyoung.oa.entity;

/**
 * @author smallyoung
 * @data 2020/11/18
 */

import com.fasterxml.jackson.annotation.JsonFormat;
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

@Getter
@Setter
@Entity
@ApiModel("用车记录")
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
    @JoinColumn(name = "vehicle_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ApiModelProperty(notes = "车辆信息")
    private VehicleInformation vehicleInformation;

    /**
     * 操作人
     */
    @CreatedBy
    @Column(name = "username" )
    @ApiModelProperty(notes = "操作人")
    private String username;

    /**
     * 类型
     */
    @Column(name = "type" )
    @ApiModelProperty(notes = "类型")
    private String type;

    /**
     * 备注
     */
    @Column(name = "remarks" )
    @ApiModelProperty(notes = "备注")
    private String remarks;

    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime createTime;
}
