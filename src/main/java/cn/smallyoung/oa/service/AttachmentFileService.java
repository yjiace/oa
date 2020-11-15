package cn.smallyoung.oa.service;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.AttachmentFileDao;
import cn.smallyoung.oa.entity.AttachmentFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author smallyoung
 * @data 2020/11/12
 */
@Service
@Transactional(readOnly = true)
public class AttachmentFileService extends BaseService<AttachmentFile, Long> {

    @Resource
    private AttachmentFileDao attachmentFileDao;

    @Transactional(rollbackFor = Exception.class)
    public AttachmentFile uploadFile(MultipartFile multipartFile, String documentNumber, String securityClassification) throws IOException {
        // todo  文件存储位置
        String filePath = "";
        AttachmentFile attachmentFile;
        //将文件写入磁盘
        String fileName = multipartFile.getOriginalFilename();
        String url = filePath + File.separator + IdUtil.objectId() + File.separator + FileTypeUtil.getType(fileName);
        File file = new File(url);
        multipartFile.transferTo(file);
        //保存文件基础信息到数据库
        attachmentFile = new AttachmentFile();
        attachmentFile.setUrl(url);
        attachmentFile.setName(multipartFile.getName());
        attachmentFile.setFileName(fileName);
        attachmentFile.setSize(multipartFile.getSize());
        attachmentFile.setMd5(DigestUtil.md5Hex(file));
        attachmentFile.setSecurityClassification(securityClassification);
        attachmentFile.setDocumentNumber(documentNumber);
        return attachmentFileDao.save(attachmentFile);
    }
}
