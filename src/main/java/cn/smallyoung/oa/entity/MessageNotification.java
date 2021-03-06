package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.interfaces.DataName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
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
public class MessageNotification implements Serializable {

    private static final long serialVersionUID = 3133450187488906036L;

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
    @DataName(name = "发起人用户名")
    @Column(name = "initiator_username" )
    @ApiModelProperty(notes = "发起人用户名")
    private String initiatorUsername;

    /**
     * 接收人用户名
     */
    @DataName(name = "接收人用户名")
    @Column(name = "recipient_username" )
    @ApiModelProperty(notes = "接收人用户名")
    private String recipientUsername;

    /**
     * 状态，Read：已读；unread：未读
     */
    @DataName(name = "状态")
    @Column(name = "status" )
    @ApiModelProperty(notes = "状态，Read：已读；unread：未读")
    private String status;

    /**
     * 消息通知内容
     */
    @DataName(name = "消息通知内容")
    @Column(name = "content" )
    @ApiModelProperty(notes = "消息通知内容")
    private String content;

    /**
     * 消息来源
     */
    @DataName(name = "消息来源")
    @Column(name = "source" )
    @ApiModelProperty(notes = "消息来源")
    private String source;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private LocalDateTime createTime;

    /**
     * 阅读时间
     */
    @DataName(name = "阅读时间")
    @Column(name = "reading_time")
    @ApiModelProperty(notes = "阅读时间")
    private LocalDateTime readingTme;

    /**
     * N正常，Y删除
     */
    @DataName(name = "删除标识")
    @Column(name = "is_delete")
    @ApiModelProperty(notes = "删除标识：N正常，Y删除")
    private String isDelete;
}
