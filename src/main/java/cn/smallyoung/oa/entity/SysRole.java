package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(mappedBy="roles", fetch = FetchType.LAZY)
    private List<SysUser> users;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_role_permission", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private List<SysPermission> permissions = new ArrayList<>();
}
