package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Getter
@Setter
@Entity
@ApiModel("角色对象")
@Table(name = "t_sys_role")
public class SysRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -2203702702813478824L;

    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name" )
    @ApiModelProperty(notes = "角色名称")
    private String name;

    /**
     * 备注
     */
    @Column(name = "comments" )
    @ApiModelProperty(notes = "角色备注")
    private String comments;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_role",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "username")})
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<SysUser> users;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_role_permission",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<SysPermission> permissions;
}
