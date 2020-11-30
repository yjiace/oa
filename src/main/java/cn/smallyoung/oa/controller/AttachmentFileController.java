package cn.smallyoung.oa.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.AttachmentFile;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.AttachmentFileService;
import cn.smallyoung.oa.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.rmi.AccessException;
import java.util.Collections;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/14
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/file")
@Api(tags = "文件操作")
public class AttachmentFileController {

    private final TimedCache<String, Dict> attachmentFileToken = CacheUtil.newTimedCache(1000 * 60);

    @Resource
    private AttachmentFileService attachmentFileService;
    @Resource
    private SysUserService sysUserService;

    @PostConstruct
    private void init() {
        attachmentFileToken.schedulePrune(5);
    }

    /**
     * 分页查询所有
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAll")
    @ApiOperation(value = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER_FIND')")
    public Page<AttachmentFile> findAll(@RequestParam(defaultValue = "1") Integer page,
                                        HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return attachmentFileService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 上传附件
     *
     * @param file                   附件的文件集合
     * @param securityClassification 文件密级
     */
    @PostMapping(value = "uploadFile")
    @ApiOperation(value = "上传附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "附件", dataType = "File"),
            @ApiImplicitParam(name = "documentNumber", value = "文件编号", dataType = "String"),
            @ApiImplicitParam(name = "securityClassification", value = "文件密级", dataType = "String")
    })
    @SystemOperationLog(module = "文件操作", methods = "上传文件",
            serviceClass = AttachmentFileService.class, way = SysOperationLogWayEnum.RecordOnly)
    public List<AttachmentFile> uploadFile(MultipartFile file, String documentNumber, String securityClassification) {
        return attachmentFileService.uploadFile(Collections.singletonList(file), documentNumber, securityClassification);
    }

    /**
     * 获取下载凭证
     */
    @GetMapping(value = "getToken")
    @ApiOperation(value = "获取下载凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "附件主键id")
    })
    @SystemOperationLog(module = "文件管理", methods = "获取下载凭证",
            serviceClass = AttachmentFileService.class, way = SysOperationLogWayEnum.RecordOnly)
    public String getToken(Long id) throws FileNotFoundException {
        AttachmentFile attachmentFile = checkAttachmentFile(id);
        return createToken("download", attachmentFile);
    }

    /**
     * 获取在线浏览凭证
     */
    @GetMapping(value = "browseOnlineToken")
    @ApiOperation(value = "获取在线浏览凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "附件主键id")
    })
    public String browseOnlineToken(Long id) throws FileNotFoundException {
        AttachmentFile attachmentFile = checkAttachmentFile(id);
        return createToken("browseOnline", attachmentFile);
    }

    /**
     * 根据凭证下载附件
     *
     * @param token 附件下载凭证
     */
    @GetMapping(value = "downloadFileByToken")
    @ApiOperation(value = "根据凭证下载附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "附件下载凭证")
    })
    public void downloadFile(String token, HttpServletResponse response) throws IOException {
        if (StrUtil.isBlank(token)) {
            throw new NullPointerException("参数错误");
        }
        Dict dict = attachmentFileToken.get("download" + token);
        if (dict == null) {
            log.error("文件下载请求被拒绝，请重新请求下载文件");
            throw new AccessException("文件下载请求被拒绝，请重新请求下载文件");
        }
        upload((AttachmentFile) dict.get("file"), response);
    }

    /**
     * 根据凭证下载附件
     *
     * @param token 附件下载凭证
     */
    @GetMapping(value = "downloadFileByToken")
    @ApiOperation(value = "根据凭证下载附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "附件下载凭证"),
            @ApiImplicitParam(name = "type", value = "下载的类型，swf,pdf")
    })
    public void browseOnline(String token, String type, HttpServletResponse response) throws IOException {
        if (StrUtil.isBlank(token)) {
            throw new NullPointerException("参数错误");
        }
        Dict dict = attachmentFileToken.get("browseOnline" + token);
        if (dict == null) {
            log.error("文件下载请求被拒绝，请重新请求下载文件");
            throw new AccessException("文件下载请求被拒绝，请重新请求下载文件");
        }
        File file  = attachmentFileService.browseOnline((AttachmentFile) dict.get("file"), type);
        upload(file, file.getName(), response);
    }

    /**
     * 下载附件
     *
     * @param id 需要下载的附件的主键ID
     */
    @GetMapping(value = "downloadFile")
    @ApiOperation(value = "下载附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "附件主键id")
    })
    @SystemOperationLog(module = "文件管理", methods = "文件下载",
            serviceClass = AttachmentFileService.class, way = SysOperationLogWayEnum.RecordOnly)
    public void downloadFile(Long id, HttpServletResponse response) throws IOException {
        upload(checkAttachmentFile(id), response);
    }


    /**
     * 删除附件
     *
     * @param id 附件主键id
     */
    @DeleteMapping(value = "deleteFile")
    @ApiOperation(value = "删除附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "附件主键id")
    })
    @SystemOperationLog(module = "文件操作", methods = "删除文件",
            serviceClass = AttachmentFileService.class, way = SysOperationLogWayEnum.RecordOnly)
    public void deleteFile(Long id) throws FileNotFoundException {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        AttachmentFile attachmentFile = attachmentFileService.findOne(id);
        if (attachmentFile == null) {
            String error = String.format("根据ID【%s】,没有找到文件", id);
            log.error(error);
            throw new FileNotFoundException(error);
        }
        attachmentFile.setIsDelete("Y");
        attachmentFileService.save(attachmentFile);
    }

    private void upload(AttachmentFile attachmentFile, HttpServletResponse response) throws IOException {
        File file = new File(attachmentFile.getUrl());
        if (!file.exists()) {
            String error = String.format("根据路径【%s】,没有找到文件", attachmentFile.getUrl());
            log.error(error);
            throw new FileNotFoundException(error);
        }
        upload(file, attachmentFile.getFileName(), response);
    }
    private void upload(File file, String fileName, HttpServletResponse response) throws IOException {
        InputStream inputStream;
        OutputStream outputStream;
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        inputStream = new FileInputStream(file);
        outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int length;
        while ((length = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, length);
        }
        outputStream.close();
        inputStream.close();
    }

    private String createToken(String type, AttachmentFile attachmentFile){
        String token = IdUtil.simpleUUID();
        attachmentFileToken.put(type + token, Dict.create()
                .set("user", sysUserService.currentlyLoggedInUser()).set("file", attachmentFile));
        return token;
    }

    //todo 操作日志

    private AttachmentFile checkAttachmentFile(Long id) throws FileNotFoundException {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        AttachmentFile attachmentFile = attachmentFileService.findOne(id);
        String isDelete = "Y";
        if (attachmentFile == null || isDelete.equals(attachmentFile.getIsDelete())) {
            String error = String.format("根据ID【%s】,没有找到文件", id);
            log.error(error);
            throw new FileNotFoundException(error);
        }
        //todo 权限校验

        return attachmentFile;
    }
}
