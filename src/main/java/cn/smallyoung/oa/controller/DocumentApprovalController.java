package cn.smallyoung.oa.controller;

import cn.hutool.core.collection.CollectionUtil;
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
@RequestMapping("/approval/document")
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
        return documentApprovalService.findAll(map, PageRequest.of(page - 1, limit,
                Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    /**
     * 查询需要我审批的审批
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findAllApprovalRequired")
    @ApiOperation(value = "查询需要我审批的审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    public Page<DocumentApproval> findAllApprovalRequired(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer limit) {
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
        return checkApproval(id);
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
    @SystemOperationLog(module = "审批", methods = "操作审批", serviceClass = DocumentApprovalService.class,
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
        DocumentApproval approval = documentApprovalService.findOne(id);
        if (approval == null) {
            String error = String.format("根据ID【%s】没有查询到该审批", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        String username = sysUserService.currentlyLoggedInUser();
        if(!approval.getNode().getUser().equals(username)){
            String error = String.format("您【%s】当前不可对本次审批【%s】进行操作", username, id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return completed.equals(operation) ? documentApprovalService.completedApproval(checkApproval(id))
                : documentApprovalService.rejectedApproval(approval);
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
    @SystemOperationLog(module = "审批", methods = "重新审批", serviceClass = DocumentApprovalService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public DocumentApproval reApprove(Long id) {
        DocumentApproval approval = checkApproval(id);
        String rejected = "Rejected";
        String withdrawn = "Withdrawn";
        if (!rejected.equals(approval.getStatus()) && !withdrawn.equals(approval.getStatus())) {
            String error = String.format("当前状态【%s】的审批【%s】，不支持重新审批操作", approval.getStatus(), id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return documentApprovalService.reApprove(approval);
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
    @SystemOperationLog(module = "审批", methods = "撤回审批", serviceClass = DocumentApprovalService.class,
            queryMethod = "findOne", parameterType = "Long", parameterKey = "id")
    public DocumentApproval withdrawalOfApproval(Long id) {
        return documentApprovalService.withdrawalOfApproval(checkApproval(id));
    }

    /**
     * 提交审批
     */
    @PostMapping("submitForApproval")
    @ApiOperation(value = "提交审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds", value = "上传的文件ID列表", dataType = "List"),
            @ApiImplicitParam(name = "file", value = "上传的文件列表", dataType = "List"),
            @ApiImplicitParam(name = "securityClassification", value = "文件：密级指定", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名列表", dataType = "List", required = true),
            @ApiImplicitParam(name = "title", value = "标题", dataType = "String", required = true),
            @ApiImplicitParam(name = "templateHtml", value = "模板HTML", dataType = "String"),
            @ApiImplicitParam(name = "organizer", value = "承办单位", dataType = "String"),
            @ApiImplicitParam(name = "extra", value = "额外信息，JSON格式", dataType = "String")
    })
    @SystemOperationLog(module = "审批", methods = "提交审批",
            serviceClass = DocumentApprovalService.class, way = SysOperationLogWayEnum.UserAfter)
    public DocumentApproval submitForApproval(DocumentApprovalVO documentApprovalVO) {
        if(CollectionUtil.isEmpty(documentApprovalVO.getUsername())){
            throw new NullPointerException("参数错误");
        }
        return documentApprovalService.submitForApproval(documentApprovalVO);
    }


    private DocumentApproval checkApproval(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        DocumentApproval approval = documentApprovalService.findOne(id);
        if (approval == null) {
            String error = String.format("根据ID【%s】没有查询到该审批", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return approval;
    }
}
