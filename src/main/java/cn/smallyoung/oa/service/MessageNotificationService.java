package cn.smallyoung.oa.service;

import cn.hutool.core.collection.CollUtil;
import cn.smallyoung.oa.base.BaseService;
import cn.smallyoung.oa.dao.MessageNotificationDao;
import cn.smallyoung.oa.entity.MessageNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/11/14
 */

@Service
@Transactional(readOnly = true)
public class MessageNotificationService extends BaseService<MessageNotification, Long> {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private MessageNotificationDao messageNotificationDao;

    /**
     * 发布消息
     *
     * @param username 接受者的用户名
     * @param source   消息的来源
     * @param content  消息内容
     * @return 保存后的消息实体对象
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageNotification releaseMessage(String username, String source, String content) {
        List<MessageNotification> messageNotifications = this.releaseMessage(Collections.singletonList(username), source, content);
        return CollUtil.isNotEmpty(messageNotifications) ? messageNotifications.get(0) : null;
    }

    /**
     * 发布消息
     *
     * @param usernameList 接受者的用户名集合
     * @param source       消息的来源
     * @param content      消息内容
     * @return 保存后的消息实体对象
     */
    @Transactional(rollbackFor = Exception.class)
    public List<MessageNotification> releaseMessage(List<String> usernameList, String source, String content) {
        String currentlyLoggedInUser = sysUserService.currentlyLoggedInUser();
        List<MessageNotification> messageNotifications = new ArrayList<>();
        MessageNotification messageNotification;
        for (String username : usernameList) {
            messageNotification = new MessageNotification();
            messageNotification.setContent(content);
            messageNotification.setSource(source);
            messageNotification.setRecipientUsername(username);
            messageNotification.setIsDelete("N");
            messageNotification.setStatus("unread");
            messageNotification.setInitiatorUsername(currentlyLoggedInUser);
            messageNotifications.add(messageNotification);
        }
        return messageNotificationDao.saveAll(messageNotifications);
    }


    public Long unreadCount(String username){
        return messageNotificationDao.countByRecipientUsername(username);
    }
}
