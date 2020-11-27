package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Getter
@Setter
@Entity
@ApiModel("权限")
@Table(name = "t_sys_permission")
public class SysPermission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4357934206294056837L;

    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 上级权限
     */
    @Column(name = "parent_id" )
    @ApiModelProperty(notes = "上级权限")
    private Long parentId;
    /**
     * 权限名
     */
    @Column(name = "name" )
    @ApiModelProperty(notes = "权限名")
    private String name;
    /**
     * 权限值
     */
    @Column(name = "val" )
    @ApiModelProperty(notes = "权限值")
    private String val;

}
