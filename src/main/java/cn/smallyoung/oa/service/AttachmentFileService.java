package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.AttachmentFileDao;
import cn.smallyoung.oa.entity.AttachmentFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
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
        if (CollUtil.isEmpty(multipartFiles)) {
            return null;
        }
        List<AttachmentFile> attachmentFiles = new ArrayList<>();
        String filePath = getPath(securityClassification);
        AttachmentFile attachmentFile;
        //将文件写入磁盘
        String fileName;
        String url;
        File file;

        for (MultipartFile multipartFile : multipartFiles) {
            try {
                fileName = multipartFile.getOriginalFilename();
                if(StrUtil.isBlank(fileName) || !fileName.contains(".")){
                    continue;
                }
                url = filePath + IdUtil.objectId() + fileName.substring(fileName.lastIndexOf("."));
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
                attachmentFile.setIsDelete("N");
                attachmentFiles.add(attachmentFile);
            } catch (Exception e) {
                log.error("上传文件错误，{}", e.getMessage());
            }
        }
        return attachmentFileDao.saveAll(attachmentFiles);
    }

    public static String getPath(String subdirectory) {
        //获取跟目录---与jar包同级目录的upload目录下指定的子目录subdirectory
        File upload;
        try {
            //本地测试时获取到的是"工程目录/target/upload/subdirectory
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if (!path.exists()) {
                path = new File("");
            }
            upload = new File(path.getAbsolutePath(), "upload" + File.separator + subdirectory + File.separator);
            //如果不存在则创建目录
            if (!upload.exists()) {
                FileUtil.mkdir(upload);
            }
            return upload + File.separator;
        } catch (FileNotFoundException e) {
            log.error("获取服务器路径发生错误，{}", e.getMessage());
            throw new RuntimeException("获取服务器路径发生错误！");
        }
    }
}
