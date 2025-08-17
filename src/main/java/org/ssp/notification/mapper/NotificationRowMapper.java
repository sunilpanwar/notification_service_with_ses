package org.ssp.notification.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssp.notification.entity.Notification;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class NotificationRowMapper implements RowMapper<Notification> {

    Logger logger = LoggerFactory.getLogger(NotificationRowMapper.class);
    @Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notification notification = new Notification(
                rs.getLong("id"),
                rs.getString("email_batch_id"),
                rs.getString("ses_message_id"),
                rs.getString("recipient_email"),
                rs.getString("sender_email"),
                rs.getString("subject"),
                rs.getString("status"),
                rs.getString("status_details"),
                rs.getTimestamp("sent_timestamp"),
                rs.getTimestamp("delivery_timestamp"),
                rs.getTimestamp("bounce_timestamp"),
                rs.getTimestamp("complaint_timestamp"),
                rs.getString("error_code"),
                rs.getString("body")
        );
        logger.debug("Mapped Notification: {}", notification);
        return notification;
    }
}