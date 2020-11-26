package cn.smallyoung.oa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/11/18
 */
@Getter
@Setter
@ToString
@ApiModel("车辆管理")
public class VehicleInformationVO implements Serializable {

    private static final long serialVersionUID = -4103150040066001046L;

    @ApiModelProperty(notes = "主键ID")
    private Long id;

    @ApiModelProperty(notes = "车辆名称")
    private String name;

    @ApiModelProperty(notes = "车辆编号")
    private String number;

    @ApiModelProperty(notes = "车牌号")
    private String plateNumber;

    @ApiModelProperty(notes = "所属单位")
    private String company;

    @ApiModelProperty(notes = "车辆型号")
    private String model;
}
