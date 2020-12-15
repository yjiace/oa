package cn.smallyoung.oa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/12/15
 */
@Getter
@Setter
@Entity
@Table(name = "t_index")
public class Index implements Serializable {

    private static final long serialVersionUID = 4107030020867481674L;

    @Id
    @Column(name = "name" )
    private String name;

    @Column(name = "max_id" )
    private Long maxId;

    @Column(name = "years" )
    private String years;
}
