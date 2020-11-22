package cn.smallyoung.oa.dao;

import cn.smallyoung.oa.base.BaseDao;
import cn.smallyoung.oa.entity.MessageNotification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageNotificationDao extends BaseDao<MessageNotification, Long> {

    @Query(value = "select count(*) from t_message_notification where recipient_username = ?1 and status = 'unread' and is_delete = 'N' order by create_time desc ", nativeQuery = true)
    Long countByRecipientUsername(String recipientUsername);

    @Modifying
    @Query(value = "update t_message_notification set status = 'Read' where recipient_username = ?1 and status = 'unread' ", nativeQuery = true)
    Long markReadWithOneClick(String username);
}
