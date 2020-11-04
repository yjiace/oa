package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

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
@Table(name = "t_sys_permission")
public class SysPermission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4357934206294056837L;

    @Id
    @Column(name = "id" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 上级权限
     */
    @Column(name = "parent_id" )
    private Long parentId;
    /**
     * 权限名
     */
    @Column(name = "name" )
    private String name;
    /**
     * 权限值
     */
    @Column(name = "val" )
    private String val;


    @JsonIgnore
    @Where(clause = " is_delete = 'N' ")
    @ManyToMany(mappedBy="permissions", fetch = FetchType.LAZY)
    private List<SysRole> roles;


}
