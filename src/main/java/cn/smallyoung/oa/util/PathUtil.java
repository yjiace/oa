package cn.smallyoung.oa.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author smallyoung
 * @data 2020/11/30
 */
@Slf4j
public class PathUtil {

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
