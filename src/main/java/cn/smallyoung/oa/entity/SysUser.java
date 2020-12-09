package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import cn.smallyoung.oa.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
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

    /**
     * 用户名
     */
    @Id
    @DataName(name = "用户名")
    @Column(name = "user_name" )
    @ApiModelProperty(notes = "用户名")
    @GeneratedValue(generator = "user_name_assigned")
    @GenericGenerator(name = "user_name_assigned", strategy = "assigned")
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
     * 单位
     */
    @Column(name = "company" )
    @DataName(name = "单位")
    @ApiModelProperty(notes = "单位")
    private String company;

    /**
     * 科室
     */
    @Column(name = "section" )
    @DataName(name = "科室")
    @ApiModelProperty(notes = "科室")
    private String section;

    /**
     * 职位
     */
    @Column(name = "position" )
    @DataName(name = "职位")
    @ApiModelProperty(notes = "职位")
    private String position;

    /**
     * 部门
     */
    @Column(name = "department" )
    @DataName(name = "部门")
    @ApiModelProperty(notes = "部门")
    private String department;

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
    @DataName(name = "角色")
    @ApiModelProperty(hidden = true)
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_role", joinColumns = {@JoinColumn(name = "user_name")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<SysRole> roles = new ArrayList<>();

    @Transient
    private List<String> roleNames = new ArrayList<>();

    public List<String> getRoleNames() {
        return roles.stream().map(SysRole::getName).collect(Collectors.toList());
    }

    @Override
    @Transient
    @ApiModelProperty(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(SysRole::getSysPermissions)
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
