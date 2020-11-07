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
    private long id;

    /**
     * 用户名
     */
    @CreatedBy
    @Column(name = "username" )
    @ApiModelProperty(notes = "用户名")
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
     * 操作前参数
     */
    @Column(name = "before_params" )
    @ApiModelProperty(notes = "操作前参数")
    private String beforeParams;

    /**
     * 操作时请求参数
     */
    @Column(name = "operate_params" )
    @ApiModelProperty(notes = "操作时请求参数")
    private String operateParams;

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
     * 操作状态描述
     */
    @Column(name = "result_status" )
    @ApiModelProperty(notes = "操作状态")
    private String resultStatus;

    /**
     * 操作结果描述
     */
    @Column(name = "result_msg" )
    @ApiModelProperty(notes = "操作结果")
    private String resultMsg;

}


