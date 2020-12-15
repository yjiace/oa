package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.AttachmentFileDao;
import cn.smallyoung.oa.entity.AttachmentFile;
import cn.smallyoung.oa.util.OfficeConverter;
import cn.smallyoung.oa.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/12
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class AttachmentFileService extends BaseService<AttachmentFile, Long> {

    private final static List<String> NEED_CONVERTER = Arrays.asList("doc", "docx", "xls", "xlsx","ppt","pptx", "odt", "ods", "odp");

    @Resource
    private OfficeConverter officeConverter;
    @Resource
    private AttachmentFileDao attachmentFileDao;

    public List<AttachmentFile> findByIdIn(List<Long> idList){
        return CollectionUtil.isNotEmpty(idList) ? attachmentFileDao.findByIdIn(idList) : new ArrayList<>();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<AttachmentFile> uploadFile(List<MultipartFile> multipartFiles, String securityClassification) {
        if (CollUtil.isEmpty(multipartFiles)) {
            return new ArrayList<>();
        }
        List<AttachmentFile> attachmentFiles = new ArrayList<>();
        String filePath = PathUtil.getPath(securityClassification);
        AttachmentFile attachmentFile;
        //将文件写入磁盘
        String uploadFileName;
        String fileName;
        File file;

        for (MultipartFile multipartFile : multipartFiles) {
            try {
                uploadFileName = multipartFile.getOriginalFilename();
                if(StrUtil.isBlank(uploadFileName) || !uploadFileName.contains(".")){
                    continue;
                }
                fileName = IdUtil.objectId() + uploadFileName.substring(uploadFileName.lastIndexOf("."));
                file = new File(filePath + fileName);
                multipartFile.transferTo(file);
                //保存文件基础信息到数据库
                attachmentFile = new AttachmentFile();
                attachmentFile.setUploadFileName(uploadFileName);
                attachmentFile.setFileName(fileName);
                attachmentFile.setSize(multipartFile.getSize());
                attachmentFile.setMd5(DigestUtil.md5Hex(file));
                attachmentFile.setSecurityClassification(securityClassification);
                attachmentFile.setIsDelete("N");
                attachmentFiles.add(attachmentFile);
            } catch (Exception e) {
                log.error("上传文件错误，{}", e.getMessage());
            }
        }
        attachmentFileDao.saveAll(attachmentFiles);
        converter(attachmentFiles);
        return attachmentFiles;
    }

    @Async
    public void converter(List<AttachmentFile> attachmentFiles){
        if(CollUtil.isEmpty(attachmentFiles)){
            return;
        }
        File officeFile;
        File pdfFile;
        File swfFile;
        String fileName;
        String pdfName;
        String swfName;
        for(AttachmentFile attachmentFile : attachmentFiles){
            fileName = attachmentFile.getFileName();
            if(!NEED_CONVERTER.contains(fileName.substring(fileName.lastIndexOf(".") + 1))){
                continue;
            }
            officeFile = new File(fullPath(attachmentFile.getSecurityClassification(), fileName));
            pdfName = fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
            pdfFile = new File(fullPath(attachmentFile.getSecurityClassification(), pdfName));
            if(!officeConverter.office2pdf(officeFile, pdfFile)){
                continue;
            }
            attachmentFile.setPdfName(pdfName);
            swfName = fileName.substring(0, fileName.lastIndexOf(".")) + ".swf";
            swfFile = new File(fullPath(attachmentFile.getSecurityClassification(), swfName));
            if(!officeConverter.pdf2swf(pdfFile, swfFile)){
                continue;
            }
            attachmentFile.setSwfName(swfName);
        }
        attachmentFileDao.saveAll(attachmentFiles);
    }

    public File browseOnline(AttachmentFile attachmentFile, String type){
        attachmentFile = attachmentFileDao.findById(attachmentFile.getId()).orElse(attachmentFile);
        String url = fullPath(attachmentFile.getSecurityClassification(), attachmentFile.getFileName());
        if(!NEED_CONVERTER.contains(url.substring(url.lastIndexOf(".") + 1))){
            return new File(url);
        }
        File pdfFile = new File(url.substring(0, url.lastIndexOf(".")) + ".pdf");
        String pdf = "pdf";
        if(!pdfFile.isFile()){
            officeConverter.office2pdf(new File(url), pdfFile);
        }
        if(pdf.equals(type)){
            return pdfFile;
        }
        File swfFile = new File(url.substring(0, url.lastIndexOf(".")) + ".swf");
        if(!swfFile.isFile()){
            officeConverter.office2pdf(pdfFile, swfFile);
        }
        return swfFile;
    }

    public String fullPath(String securityClassification, String fileName){
        return PathUtil.getPath(securityClassification) + fileName;
    }
}
