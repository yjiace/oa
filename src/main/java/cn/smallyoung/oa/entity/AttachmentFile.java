package cn.smallyoung.oa.entity;

import cn.smallyoung.oa.base.BaseEntity;
import cn.smallyoung.oa.interfaces.DataName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author smallyoung
 * @data 2020/11/11
 */

@Getter
@Setter
@Entity
@ApiModel("附件文件")
@Table(name = "t_attachment_file")
@EntityListeners({AuditingEntityListener.class})
public class AttachmentFile extends BaseEntity implements Serializable {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id" )
    @ApiModelProperty(notes = "主键ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 系统文件名
     */
    @Column(name = "upload_file_name" )
    @DataName(name = "上传文件名")
    @ApiModelProperty(notes = "上传文件名")
    private String uploadFileName;

    @Column(name = "file_name" )
    @DataName(name = "系统文件名")
    @ApiModelProperty(notes = "系统文件名")
    private String fileName;

    /**
     * MD5
     */
    @Column(name = "md5" )
    @DataName(name = "MD5")
    @ApiModelProperty(notes = "MD5")
    private String md5;

    /**
     * 文件大小
     */
    @Column(name = "size" )
    @DataName(name = "文件大小")
    @ApiModelProperty(notes = "文件大小")
    private Long size;

    /**
     * swf路径
     */
    @Column(name = "swf_name" )
    @DataName(name = "swf路径")
    @ApiModelProperty(notes = "swf路径")
    private String swfName;

    /**
     * pdf路径
     */
    @Column(name = "pdf_name" )
    @DataName(name = "pdf路径")
    @ApiModelProperty(notes = "pdf路径")
    private String pdfName;



    /**
     * 文件编号
     */
    @DataName(name = "文件编号")
    @Column(name = "document_number" )
    @ApiModelProperty(notes = "文件编号")
    private String documentNumber;

    /**
     * 密级指定
     */
    @DataName(name = "密级指定")
    @Column(name = "security_classification" )
    @ApiModelProperty(notes = "密级指定")
    private String securityClassification;
}
