package cn.smallyoung.oa.entity;

import cn.hutool.core.annotation.PropIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author smallyoung
 * @data 2020/11/21
 */
@Getter
@Setter
@Entity
@ApiModel("文件审批日志")
@Table(name = "t_document_approval_log")
@EntityListeners({AuditingEntityListener.class})
public class DocumentApprovalLog {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发起人用户名
     */
    @Column(name = "username")
    @ApiModelProperty(notes = "发起人用户名")
    private String username;

    /**
     * 关联的审批
     */
    @ManyToOne
    @JsonIgnore
    @PropIgnore
    @JoinColumn(name = "document_approval_id")
    private DocumentApproval documentApproval;

    /**
     * 操作记录的ID
     */
    @Column(name = "operation_id")
    @ApiModelProperty(notes = "操作记录的ID")
    private Long operationId;

    /**
     * 执行的操作，add：增加，reApprove：重新审批，rejected：拒绝审批，withdrawal：撤回审批，agree：同意审批
     */
    @Column(name = "operation")
    @ApiModelProperty(notes = "执行的操作，add：增加，reApprove：重新审批，rejected：拒绝审批，withdrawal：撤回审批，agree：同意审批")
    private String operation;

    /**
     * 操作类型，attachmentFile：附件，documentApproval：审批，documentApprovalComments：评论
     */
    @Column(name = "operation_type")
    @ApiModelProperty(notes = "操作类型，attachmentFile：附件，documentApproval：审批，documentApprovalComments：评论")
    private String operationType;

    /**
     * 操作信息
     */
    @Column(name = "operation_message")
    @ApiModelProperty(notes = "操作信息")
    private String operationMessage;

    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private LocalDateTime createTime;
}
