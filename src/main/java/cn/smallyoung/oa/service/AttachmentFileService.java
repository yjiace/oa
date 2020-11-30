package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
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

    @Transactional(rollbackFor = Exception.class)
    public List<AttachmentFile> uploadFile(List<MultipartFile> multipartFiles, String documentNumber, String securityClassification) {
        if (CollUtil.isEmpty(multipartFiles)) {
            return null;
        }
        List<AttachmentFile> attachmentFiles = new ArrayList<>();
        String filePath = PathUtil.getPath(securityClassification);
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
        String url;
        String pdfUrl;
        String swfUrl;
        boolean isOk;
        for(AttachmentFile attachmentFile : attachmentFiles){
            url = attachmentFile.getUrl();
            if(!NEED_CONVERTER.contains(url.substring(url.lastIndexOf(".") + 1))){
                continue;
            }
            officeFile = new File(attachmentFile.getUrl());
            pdfUrl = url.substring(0, url.lastIndexOf(".")) + ".pdf";
            pdfFile = new File(pdfUrl);
            if(!officeConverter.office2pdf(officeFile, pdfFile)){
                continue;
            }
            attachmentFile.setPdfUrl(pdfUrl);
            swfUrl = url.substring(0, url.lastIndexOf(".")) + ".swf";
            swfFile = new File(swfUrl);
            if(!officeConverter.pdf2swf(pdfFile, swfFile)){
                continue;
            }
            attachmentFile.setSwfUrl(swfUrl);
        }
        attachmentFileDao.saveAll(attachmentFiles);
    }

    public File browseOnline(AttachmentFile attachmentFile, String type){
        attachmentFile = attachmentFileDao.findById(attachmentFile.getId()).orElse(attachmentFile);
        String url = attachmentFile.getUrl();
        if(!NEED_CONVERTER.contains(url.substring(url.lastIndexOf(".") + 1))){
            return new File(attachmentFile.getUrl());
        }
        File pdfFile = new File(url.substring(0, url.lastIndexOf(".")) + ".pdf");
        String pdf = "pdf";
        if(!pdfFile.isFile()){
            officeConverter.office2pdf(new File(attachmentFile.getUrl()), pdfFile);
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
}
