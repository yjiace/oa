package cn.smallyoung.oa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息通知
 * @author smallyoung
 * @data 2020/11/14
 */
@Getter
@Setter
@Entity
@ApiModel("消息通知")
@Table(name = "t_message_notification")
@EntityListeners({AuditingEntityListener.class})
public class MessageNotification {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发起人用户名
     */
    @Column(name = "initiator_username" )
    @ApiModelProperty(notes = "发起人用户名")
    private String initiatorUsername;

    /**
     * 接收人用户名
     */
    @Column(name = "recipient_username" )
    @ApiModelProperty(notes = "接收人用户名")
    private String recipientUsername;

    /**
     * 状态，Read：已读；unread：未读
     */
    @Column(name = "status" )
    @ApiModelProperty(notes = "状态，Read：已读；unread：未读")
    private String status;

    /**
     * 消息通知内容
     */
    @Column(name = "content" )
    @ApiModelProperty(notes = "消息通知内容")
    private String content;

    /**
     * 消息来源
     */
    @Column(name = "source" )
    @ApiModelProperty(notes = "消息来源")
    private String source;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime createTime;

    /**
     * 阅读时间
     */
    @Column(name = "reading_time")
    @ApiModelProperty(notes = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private LocalDateTime readingTme;

    /**
     * N正常，Y删除
     */
    @Column(name = "is_delete")
    @ApiModelProperty(notes = "删除标识：N正常，Y删除")
    private String isDelete;
}
