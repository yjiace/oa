package cn.smallyoung.oa.entity;

import cn.hutool.core.annotation.PropIgnore;
import cn.smallyoung.oa.interfaces.DataName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @author smallyoung
 * @data 2020/11/21
 */
@Getter
@Setter
@Entity
@ApiModel("审批日志")
@Table(name = "t_approval_log")
@EntityListeners({AuditingEntityListener.class})
public class ApprovalLog implements Serializable {

    private static final long serialVersionUID = 1480392011564184578L;

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
    @DataName(name = "发起人用户名")
    @Column(name = "username")
    @ApiModelProperty(notes = "发起人用户名")
    private String username;

    /**
     * 关联的审批
     */
    @ManyToOne
    @JsonIgnore
    @PropIgnore
    @JoinColumn(name = "approval_id")
    private Approval approval;

    /**
     * 操作记录的ID
     */
    @DataName(name = "操作记录的ID")
    @Column(name = "operation_id")
    @ApiModelProperty(notes = "操作记录的ID")
    private Long operationId;

    /**
     * 执行的操作，add：增加，reApprove：重新审批，rejected：拒绝审批，withdrawal：撤回审批，agree：同意审批
     */
    @DataName(name = "执行的操作")
    @Column(name = "operation")
    @ApiModelProperty(notes = "执行的操作，add：增加，reApprove：重新审批，rejected：拒绝审批，withdrawal：撤回审批，agree：同意审批")
    private String operation;

    /**
     * 操作类型，attachmentFile：附件，approval：审批，approvalComments：评论
     */
    @DataName(name = "操作类型")
    @Column(name = "operation_type")
    @ApiModelProperty(notes = "操作类型，attachmentFile：附件，approval：审批，approvalComments：评论")
    private String operationType;

    /**
     * 操作信息
     */
    @DataName(name = "操作信息")
    @Column(name = "operation_message")
    @ApiModelProperty(notes = "操作信息")
    private String operationMessage;

    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private LocalDateTime createTime;
}
