package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import cn.smallyoung.oa.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author smallyoung
 * @date 2020/10/26
 */
@Getter
@Setter
@Entity
@ApiModel("用户对象")
@Table(name = "t_sys_user")
public class SysUser extends BaseEntity implements Serializable, UserDetails {


    private static final long serialVersionUID = -1419339429097967881L;

    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @DataName(name = "用户名")
    @Column(name = "user_name" )
    @ApiModelProperty(notes = "用户名")
    private String username;

    /**
     * 姓名
     */
    @Column(name = "name" )
    @DataName(name = "姓名")
    @ApiModelProperty( notes = "姓名")
    private String name;
    /**
     * 手机号
     */
    @Column(name = "phone" )
    @DataName(name = "手机号")
    @ApiModelProperty(notes = "手机号")
    private String phone;

    /**
     * 电话
     */
    @Column(name = "mobile" )
    @DataName(name = "电话")
    @ApiModelProperty(notes = "电话")
    private String mobile;

    /**
     * 状态，Y正常，N冻结
     */
    @Column(name = "status" )
    @DataName(name = "状态")
    @ApiModelProperty(notes = "状态，Y正常，N冻结")
    private String status;

    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password" )
    private String password;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<SysRole> roles = new ArrayList<>();

    @Override
    @Transient
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(SysRole::getPermissions)
                .flatMap(Collection::stream)
                .filter(p -> "N".equals(p.getIsDelete()))
                .map(SysPermission::getVal).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * 帐号是否不过期，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    @ApiModelProperty(hidden = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 帐号是否不锁定，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    @ApiModelProperty(hidden = true)
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证是否不过期，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    @ApiModelProperty(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 该帐号是否启用，false则验证不通过
     */
    @JsonIgnore
    @Override
    @Transient
    @ApiModelProperty(hidden = true)
    public boolean isEnabled() {
        return true;
    }
}
