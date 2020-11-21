package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.AttachmentFileDao;
import cn.smallyoung.oa.entity.AttachmentFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/12
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AttachmentFileService extends BaseService<AttachmentFile, Long> {

    @Resource
    private AttachmentFileDao attachmentFileDao;

    @Transactional(rollbackFor = Exception.class)
    public List<AttachmentFile> uploadFile(List<MultipartFile> multipartFiles, String documentNumber, String securityClassification) {
        if(CollUtil.isEmpty(multipartFiles)){
            return null;
        }
        List<AttachmentFile> attachmentFiles = new ArrayList<>();
        // todo  文件存储位置
        String filePath = "";
        AttachmentFile attachmentFile;
        //将文件写入磁盘
        String fileName;
        String url;
        File file;

        for (MultipartFile multipartFile : multipartFiles) {
            try {
                fileName = multipartFile.getOriginalFilename();
                url = filePath + File.separator + IdUtil.objectId() + File.separator + FileTypeUtil.getType(fileName);
                file = new File(url);
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
                attachmentFiles.add(attachmentFile);
            }catch (Exception e){
                log.error("上传文件错误，{}", e.getMessage());
            }
        }
        return attachmentFileDao.saveAll(attachmentFiles);
    }
}
