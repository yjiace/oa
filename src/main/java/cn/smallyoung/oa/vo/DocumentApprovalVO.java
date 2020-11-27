package cn.smallyoung.oa.vo;

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
public class DocumentApprovalVO implements Serializable {

    private static final long serialVersionUID = -5071610822869072373L;

    /**
     * 上传的文件列表
     */
    List<MultipartFile> file;

    /**
     * 用户名列表
     */
    List<String> username;

    /**
     * 文件编号
     */
    private String documentNumber;

    /**
     * 密级指定
     */
    private String securityClassification;

    /**
     * 标题
     */
    private String title;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 编号
     */
    private String number;
}
