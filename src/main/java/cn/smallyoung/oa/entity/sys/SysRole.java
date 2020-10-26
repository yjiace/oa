package cn.smallyoung.oa.entity.sys;

import cn.smallyoung.oa.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "t_sys_role")
public class SysRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -2203702702813478824L;

    @Id
    @Column(name = "id" )
    private String id;

    @Column(name = "name" )
    private String name;

    /**
     * 备注
     */
    @Column(name = "comments" )
    private String comments;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_sys_user_role",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    @JsonIgnore
    private List<SysUser> users;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_sys_role_permission",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    @JsonIgnore
    private List<SysPermission> permissions;
}
