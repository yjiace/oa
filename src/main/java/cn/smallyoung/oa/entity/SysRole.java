package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import cn.smallyoung.oa.interfaces.DataName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @DataName(name = "角色名称")
    @Column(name = "name" )
    @ApiModelProperty(notes = "角色名称")
    private String name;

    /**
     * 备注
     */
    @DataName(name = "角色备注")
    @Column(name = "comments" )
    @ApiModelProperty(notes = "角色备注")
    private String comments;

    @ApiModelProperty(hidden = true)
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_role_permission", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private List<SysPermission> sysPermissions = new ArrayList<>();


    @Transient
    @DataName(name = "权限")
    private List<String> permissionNames = new ArrayList<>();

    public List<String> getPermissionNames() {
        return sysPermissions.stream().map(SysPermission::getName).collect(Collectors.toList());
    }
}
