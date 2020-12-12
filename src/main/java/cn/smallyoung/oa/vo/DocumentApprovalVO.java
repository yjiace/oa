package cn.smallyoung.oa.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/20
 */
@Getter
@Setter
@ToString
@ApiModel("文件审批VO")
public class DocumentApprovalVO implements Serializable {

    private static final long serialVersionUID = -5071610822869072373L;

    /**
     * 上传的文件列表
     */
    @ApiModelProperty(notes = "上传的文件列表")
    List<MultipartFile> file;

    /**
     * 用户名列表
     */
    @ApiModelProperty(notes = "用户名列表")
    List<String> username;

    /**
     * 密级指定
     */
    @ApiModelProperty(notes = "密级指定")
    private String securityClassification;

    /**
     * 标题
     */
    @ApiModelProperty(notes = "标题")
    private String title;

    /**
     * 呈批件HTML
     */
    @ApiModelProperty(notes = "呈批件HTML")
    private String templateHtml;

    /**
     * 额外信息，JSON格式
     */
    @ApiModelProperty(notes = "额外信息，JSON格式")
    private String extra;

    @ApiModelProperty(notes = "承办单位")
    private String organizer;

}
