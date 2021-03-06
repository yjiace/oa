package cn.smallyoung.oa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/11/17
 */
@Getter
@Setter
@ToString
@ApiModel("用户VO")
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 6820699603389204154L;

    /**
     * 用户名
     */
    @ApiModelProperty(notes = "用户名")
    private String username;

    /**
     * 姓名
     */
    @ApiModelProperty(notes = "姓名")
    private String name;
    /**
     * 手机号
     */
    @ApiModelProperty(notes = "手机号")
    private String phone;

    /**
     * 电话
     */
    @ApiModelProperty(notes = "电话")
    private String mobile;

    /**
     * 单位
     */
    @ApiModelProperty(notes = "单位")
    private String company;

    /**
     * 科室
     */
    @ApiModelProperty(notes = "科室")
    private String section;

    /**
     * 职位
     */
    @ApiModelProperty(notes = "职位")
    private String position;

    /**
     * 部门
     */
    @ApiModelProperty(notes = "部门")
    private String department;
}
