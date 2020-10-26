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
@Table(name = "t_sys_user")
public class SysUser extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -1419339429097967881L;
    /**
     * 主键
     */
    @Id
    @Column(name = "id" )
    private String id;

    /**
     * 用户名
     */
    @Column(name = "username" )
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password" )
    private String password;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "t_sys_user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    @JsonIgnore
    private List<SysRole> role;

}
