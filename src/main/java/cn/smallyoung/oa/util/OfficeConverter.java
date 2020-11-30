package cn.smallyoung.oa.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * office 文件格式转换
 *
 * @author smallyoung
 * @data 2020/11/30
 */
@Slf4j
@Component
public class OfficeConverter {

    @Value("${pdf2swf-exe-url}")
    private String pdf2swfExeUrl;

    public boolean office2swf(File officeFile, File swfFile) throws IOException {
        File pdfFile = new File(PathUtil.getPath("tem") + IdUtil.objectId() + ".pdf");
        boolean isOk = office2pdf(officeFile, pdfFile);
        if(isOk){
            return pdf2swf(pdfFile, swfFile);
        }
        return false;
    }
    public boolean office2pdf(File inputFile, File outputFile) {
        if (!inputFile.isFile()) {
            return false;
        }
        LocalOfficeManager officeManager = LocalOfficeManager.install();
        try {
            officeManager.start();
            JodConverter
                    .convert(inputFile)
                    .to(outputFile)
                    .execute();
            return true;
        } catch (OfficeException e) {
            log.error("office文件转PDF错误：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            OfficeUtils.stopQuietly(officeManager);
        }
        return false;
    }

    public boolean pdf2swf(File pdfFile, File swfFile) {
        if (!pdfFile.isFile()) {
            return false;
        }
        String cmd = pdf2swfExeUrl + " " + pdfFile.getPath() + " -o " + swfFile.getPath() + " -T 9";
        log.info("office文件转SWF命令：{}", cmd);
        String p = RuntimeUtil.execForStr(cmd);
        log.info("office文件转SWF结果：{}", p);
        return true;
    }
}
