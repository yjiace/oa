package cn.smallyoung.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.oa.entity.DocumentApproval;
import cn.smallyoung.oa.entity.SysOperationLogWayEnum;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.interfaces.SystemOperationLog;
import cn.smallyoung.oa.service.DocumentApprovalService;
import cn.smallyoung.oa.service.SysUserService;
import cn.smallyoung.oa.vo.DocumentApprovalVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author smallyoung
 * @data 2020/11/20
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/document/approval")
@Api(tags = "文件审批")
public class DocumentApprovalController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private DocumentApprovalService documentApprovalService;

    /**
     * 查询我提交的审批
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
    public Page<DocumentApproval> findAll(@RequestParam(defaultValue = "1") Integer page,
                                          HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "search_");
        map.put("AND_EQ_initiatorUsername", sysUserService.currentlyLoggedInUser());
        //todo 排序
        return documentApprovalService.findAll(map, PageRequest.of(page - 1, limit,
                Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 查询需要我审批的文件审批
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAllApprovalRequired")
    @ApiOperation(value = "查询需要我审批的文件审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    public Page<DocumentApproval> findAllApprovalRequired(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        return documentApprovalService.findAllApprovalRequired(page, limit);
    }

    /**
     * 根据ID查询审批详情
     *
     * @param id 审批的主键ID
     */
    @GetMapping(value = "findOneById")
    @ApiOperation(value = "根据ID查询审批详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审批的主键ID", dataType = "Long")
    })
    public DocumentApproval findOneById(Long id) {
        return checkDocumentApproval(id);
    }

    /**
     * 检查审批编号是否存在
     * @param number  审批编号
     */
    @GetMapping("checkNumber")
    @ApiOperation(value = "检查审批编号是否存在")
    @ApiImplicitParam(name = "number", value = "审批编号", required = true, dataType = "String")
    public boolean checkNumber(String number){
        return documentApprovalService.checkNumber(number);
    }

    /**
     * 操作审批
     *
     * @param id        审批的主键ID
     * @param operation 操作，Completed：同意、Rejected：拒绝
     */
    @PostMapping("operationApproval")
    @ApiOperation(value = "操作审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审批的主键ID", dataType = "Long"),
            @ApiImplicitParam(name = "operation", value = "操作，Completed：同意、Rejected：拒绝", dataType = "String")
    })
    @SystemOperationLog(module = "文件审批", methods = "操作审批", serviceClass = DocumentApprovalService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public DocumentApproval operationApproval(Long id, String operation) {
        if (id == null || StrUtil.isBlank(operation)) {
            throw new NullPointerException("参数错误");
        }
        String completed = "Completed";
        String rejected = "Rejected";
        if (!completed.equals(operation) && !rejected.equals(operation)) {
            String error = String.format("审批【%s】，不支持的操作【%s】", id, operation);
            log.error(error);
            throw new RuntimeException(error);
        }
        DocumentApproval documentApproval = documentApprovalService.findOne(id);
        if (documentApproval == null) {
            String error = String.format("根据ID【%s】没有查询到该审批", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        String username = sysUserService.currentlyLoggedInUser();
        if(!documentApproval.getNode().getUser().equals(username)){
            String error = String.format("您【%s】当前不可对本次文件审批【%s】进行操作", username, id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return completed.equals(operation) ? documentApprovalService.completedApproval(checkDocumentApproval(id))
                : documentApprovalService.rejectedApproval(documentApproval);
    }

    /**
     * 重新审批（被拒绝）
     *
     * @param id 审批的主键ID
     */
    @PostMapping("reApprove")
    @ApiOperation(value = "重新审批（被拒绝）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审批的主键ID", dataType = "Long")
    })
    @SystemOperationLog(module = "文件审批", methods = "重新审批", serviceClass = DocumentApprovalService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public DocumentApproval reApprove(Long id) {
        DocumentApproval documentApproval = checkDocumentApproval(id);
        String rejected = "Rejected";
        if (!rejected.equals(documentApproval.getStatus())) {
            String error = String.format("当前状态【%s】的审批【%s】，不支持重新审批操作", documentApproval.getStatus(), id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return documentApprovalService.reApprove(documentApproval);
    }


    /**
     * 撤回审批
     *
     * @param id 审批的主键ID
     */
    @PostMapping(value = "withdrawalOfApproval")
    @ApiOperation(value = "撤回审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审批的主键ID", dataType = "Long")
    })
    @SystemOperationLog(module = "文件审批", methods = "撤回审批", serviceClass = DocumentApprovalService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public DocumentApproval withdrawalOfApproval(Long id) {
        return documentApprovalService.withdrawalOfApproval(checkDocumentApproval(id));
    }

    /**
     * 提交审批
     */
    @PostMapping("submitForApproval")
    @ApiOperation(value = "提交审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "上传的文件列表", dataType = "List"),
            @ApiImplicitParam(name = "username", value = "用户名列表", dataType = "List"),
            @ApiImplicitParam(name = "documentNumber", value = "文件编号", dataType = "String"),
            @ApiImplicitParam(name = "securityClassification", value = "密级指定", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String"),
            @ApiImplicitParam(name = "remarks", value = "备注", dataType = "String"),
            @ApiImplicitParam(name = "number", value = "审批编号", dataType = "String")
    })
    @SystemOperationLog(module = "文件审批", methods = "提交审批",
            serviceClass = DocumentApprovalService.class, way = SysOperationLogWayEnum.RecordOnly)
    public DocumentApproval submitForApproval(DocumentApprovalVO documentApprovalVO) {
        if(!documentApprovalService.checkNumber(documentApprovalVO.getNumber())){
            String error = String.format("当前审批编号[%s]重复", documentApprovalVO.getNumber());
            log.error(error);
            throw new RuntimeException(error);
        }
        return documentApprovalService.submitForApproval(documentApprovalVO);
    }

    /**
     * 添加评论
     *
     * @param id      主键ID
     * @param message 评论
     */
    @PostMapping("addComment")
    @ApiOperation(value = "添加评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long"),
            @ApiImplicitParam(name = "message", value = "评论", dataType = "String")
    })
    @SystemOperationLog(module = "文件审批", methods = "添加评论", serviceClass = DocumentApprovalService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public DocumentApproval addComment(Long id, String message) {
        if (StrUtil.isBlank(message)) {
            throw new NullPointerException("参数错误");
        }
        return documentApprovalService.addComment(checkDocumentApproval(id), message);
    }


    private DocumentApproval checkDocumentApproval(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        DocumentApproval documentApproval = documentApprovalService.findOne(id);
        if (documentApproval == null) {
            String error = String.format("根据ID【%s】没有查询到该审批", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return documentApproval;
    }
}
