package cn.smallyoung.oa.controller;

import cn.hutool.core.collection.CollUtil;
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
import java.util.List;
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
     * @param ids 主键ID
     */
    @DeleteMapping(value = "deleteMessageNotification")
    @ApiOperation(value = "删除消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键ID", dataType = "List")
    })
    public List<MessageNotification> deleteMessageNotification(@RequestParam(value = "ids") List<Long> ids) {
        if(CollUtil.isEmpty(ids)){
            throw new NullPointerException("参数错误");
        }
        List<MessageNotification> messageNotifications = messageNotificationService.findByIdIn(ids);
        if(CollUtil.isNotEmpty(messageNotifications)){
            messageNotifications.forEach(m -> m.setIsDelete("Y"));
            messageNotificationService.save(messageNotifications);
        }
        return messageNotifications;
    }


    /**
     * 阅读消息
     *
     * @param ids 主键ID列表
     */
    @PostMapping(value = "readingMessageNotification")
    @ApiOperation(value = "阅读消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键ID列表", dataType = "List")
    })
    public List<MessageNotification> readingMessageNotification(@RequestParam(value = "ids") List<Long> ids) {
        if(CollUtil.isEmpty(ids)){
            throw new NullPointerException("参数错误");
        }
        List<MessageNotification> messageNotifications = messageNotificationService.findByIdIn(ids);
        if(CollUtil.isNotEmpty(messageNotifications)){
            LocalDateTime now = LocalDateTime.now();
            messageNotifications.forEach(m -> {
                m.setStatus("Read");
                m.setReadingTme(now);
            });
            messageNotificationService.save(messageNotifications);
        }
        return messageNotifications;
    }
}
