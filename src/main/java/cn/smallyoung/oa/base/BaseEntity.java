package cn.smallyoung.oa.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

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
public class BaseEntity {

    @Column(name = "creator")
    private Long creator;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime createTime;

    @Column(name = "updater")
    private Long updater;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime updateTime;

    /**
     * N正常，Y删除
     */
    @Column(name = "is_delete")
    private String isDelete;

}
