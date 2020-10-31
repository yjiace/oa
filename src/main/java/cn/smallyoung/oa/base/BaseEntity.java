package cn.smallyoung.oa.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @date 2020/10/25
 */
@Getter
@Setter
@MappedSuperclass
@Where(clause = " is_delete = 'N'")
public class BaseEntity {

    @Column(name = "creator")
    @ApiModelProperty(notes = "创建用户")
    private Long creator;

    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime createTime;

    @Column(name = "updater")
    @ApiModelProperty(notes = "修改用户")
    private Long updater;

    @Column(name = "update_time")
    @ApiModelProperty(notes = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updateTime;

    /**
     * N正常，Y删除
     */
    @Column(name = "is_delete")
    @ApiModelProperty(notes = "删除标识：N正常，Y删除")
    private String isDelete;

}
