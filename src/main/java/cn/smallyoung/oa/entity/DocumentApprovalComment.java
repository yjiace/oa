package cn.smallyoung.oa.entity;

/**
 * @author smallyoung
 * @data 2020/11/20
 */

import cn.hutool.core.annotation.PropIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ApiModel("用户对象")
@Table(name = "t_document_approval_comment")
@EntityListeners({AuditingEntityListener.class})
public class DocumentApprovalComment {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 审批留言
     */
    @Column(name = "message")
    private String message;

    /**
     * 关联的审批
     */
    @ManyToOne
    @JsonIgnore
    @PropIgnore
    @JoinColumn(name = "document_approval_id")
    private DocumentApproval documentApproval;

    @CreatedBy
    @Column(name = "creator")
    @ApiModelProperty(notes = "创建用户")
    private String creator;

    @CreatedDate
    @Column(name = "create_time")
    @ApiModelProperty(notes = "创建时间")
    private LocalDateTime createTime;

    /**
     * N正常，Y删除
     */
    @Column(name = "is_delete")
    @ApiModelProperty(notes = "删除标识：N正常，Y删除")
    private String isDelete;

}
