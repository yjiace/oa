package cn.smallyoung.oa.controller;

import cn.smallyoung.oa.entity.MessageNotification;
import cn.smallyoung.oa.interfaces.ResponseResultBody;
import cn.smallyoung.oa.service.MessageNotificationService;
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
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smallyoung
 * @data 2020/11/14
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("/message/notification")
@Api(tags = "消息中心")
public class MessageNotificationController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private MessageNotificationService messageNotificationService;

    /**
     * 查询未读信息总数
     */
    @GetMapping(value = "findMessageCount")
    @ApiOperation(value = "查询未读信息总数")
    public Long findMessageCount(){
        return messageNotificationService.unreadCount(sysUserService.currentlyLoggedInUser());
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
    @PreAuthorize("hasRole('ROLE_MESSAGE_NOTIFICATION') or hasRole('ROLE_MESSAGE_NOTIFICATION_FIND')")
    public Page<MessageNotification> findAll(@RequestParam(defaultValue = "1") Integer page,
                                             HttpServletRequest request, @RequestParam(defaultValue = "10") Integer limit) {
        return messageNotificationService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "status", "createTime")));
    }

    /**
     * 分页查询自己的消息
     *
     * @param page  页码
     * @param limit 页数
     */
    @GetMapping(value = "findYourOwnMessageNotification")
    @ApiOperation(value = "分页查询自己的消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    public Page<MessageNotification> findYourOwnMessageNotification(@RequestParam(defaultValue = "1") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("AND_EQ_recipientUsername", sysUserService.currentlyLoggedInUser());
        map.put("AND_EQ_isDelete", "N");
        return messageNotificationService.findAll(map,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "status", "createTime")));
    }

    /**
     * 删除消息
     *
     * @param id 主键ID
     */
    @DeleteMapping(value = "deleteMessageNotification")
    @ApiOperation(value = "删除消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long")
    })
    public void deleteMessageNotification(Long id) {
        MessageNotification messageNotification = checkAuth(id);
        messageNotification.setIsDelete("Y");
        messageNotificationService.save(messageNotification);
    }


    /**
     * 阅读消息
     *
     * @param id 主键ID
     */
    @PostMapping(value = "readingMessageNotification")
    @ApiOperation(value = "阅读消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键ID", dataType = "Long")
    })
    public MessageNotification readingMessageNotification(Long id) {
        MessageNotification messageNotification = checkAuth(id);
        messageNotification.setStatus("Read");
        messageNotification.setReadingTme(LocalDateTime.now());
        return messageNotificationService.save(messageNotification);
    }

    /**
     * 一键标记已读
     */
    @PostMapping(value = "markReadWithOneClick")
    @ApiOperation(value = "一键标记已读")
    public void markReadWithOneClick(){
        messageNotificationService.markReadWithOneClick();
    }

    private MessageNotification checkAuth(Long id) {
        if (id == null) {
            throw new NullPointerException("参数错误");
        }
        MessageNotification messageNotification = messageNotificationService.findOne(id);
        String isDelete = "Y";
        if (messageNotification == null || isDelete.equals(messageNotification.getIsDelete())) {
            String error = String.format("根据ID【%s】,没有找到消息", id);
            log.error(error);
            throw new RuntimeException(error);
        }
        String username = sysUserService.currentlyLoggedInUser();
        if (!messageNotification.getRecipientUsername().equals(username)) {
            String error = String.format("当前用户【%s】无法操作该消息【%s】", username, id);
            log.error(error);
            throw new RuntimeException(error);
        }
        return messageNotification;
    }

}
