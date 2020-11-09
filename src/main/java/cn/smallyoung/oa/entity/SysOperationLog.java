package cn.smallyoung.oa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统操作日志
 *
 * @author smallyoung
 * @data 2020/11/4
 */
@Getter
@Setter
@Entity
@ApiModel("系统操作日志")
@Table(name = "t_sys_operation_log")
@EntityListeners({AuditingEntityListener.class})
public class SysOperationLog {

    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人
     */
    @CreatedBy
    @Column(name = "username" )
    @ApiModelProperty(notes = "操作人")
    private String username;

    /**
     * 操作模块
     */
    @Column(name = "module" )
    @ApiModelProperty(notes = "操作模块")
    private String module;

    /**
     * 操作类型
     */
    @Column(name = "method" )
    @ApiModelProperty(notes = "操作类型")
    private String method;

    /**
     * 请求参数
     */
    @Column(name = "params" )
    @ApiModelProperty(notes = "请求参数")
    private String params;

    /**
     * 操作前的数据
     */
    @Column(name = "before_data" )
    @ApiModelProperty(notes = "操作前的数据")
    private String beforeData;

    /**
     * 操作后的数据
     */
    @Column(name = "after_data" )
    @ApiModelProperty(notes = "操作后的数据")
    private String afterData;

    /**
     * 说明
     */
    @Column(name = "content" )
    @ApiModelProperty(notes = "说明")
    private String content;

    /**
     * 开始时间
     */
    @Column(name = "start_time" )
    @ApiModelProperty(notes = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time" )
    @ApiModelProperty(notes = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime endTime;

    /**
     * 操作状态
     */
    @Column(name = "result_status" )
    @ApiModelProperty(notes = "操作状态")
    private String resultStatus;

    /**
     * 操作结果
     */
    @Column(name = "result_msg" )
    @ApiModelProperty(notes = "操作结果")
    private String resultMsg;

}


