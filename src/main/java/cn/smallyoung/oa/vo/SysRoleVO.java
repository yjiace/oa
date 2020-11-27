package cn.smallyoung.oa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/27
 */
@Getter
@Setter
@ToString
@ApiModel("角色VO")
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 7825858590511488317L;

    @ApiModelProperty(notes = "主键ID")
    private Long id;

    @ApiModelProperty(notes = "角色名称")
    private String name;

    @ApiModelProperty(notes = "角色备注")
    private String comments;

    @ApiModelProperty(notes = "权限列表ID")
    private List<Long> permissions;
}
