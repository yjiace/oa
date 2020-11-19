package cn.smallyoung.oa.controller;

import cn.smallyoung.oa.entity.AttachmentFile;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.AttachmentFileService;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

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

    @Resource
    private AttachmentFileService attachmentFileService;

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
            @ApiImplicitParam(name = "file", value = "附件"),
            @ApiImplicitParam(name = "documentNumber", value = "文件编号", dataType = "String"),
            @ApiImplicitParam(name = "securityClassification", value = "文件密级", dataType = "String")
    })
    @SystemOperationLog(module = "文件操作", methods = "上传文件",
            serviceClass = AttachmentFileService.class, way = SysOperationLogWayEnum.RecordOnly)
    public AttachmentFile uploadFile(MultipartFile file, String documentNumber, String securityClassification) throws IOException {
        return attachmentFileService.uploadFile(file, documentNumber, securityClassification);
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
        InputStream inputStream;
        OutputStream outputStream;
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + attachmentFile.getName() + ".xlsx");
        File file = new File(attachmentFile.getUrl());
        if (!file.exists()) {
            String error = String.format("根据路径【%s】,没有找到文件", attachmentFile.getUrl());
            log.error(error);
            throw new FileNotFoundException(error);
        }
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

    //todo 文件审批

    //todo 操作日志
}
