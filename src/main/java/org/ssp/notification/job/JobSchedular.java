package org.ssp.notification.job;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.ssp.notification.Constant;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.entity.Notification;
import org.ssp.notification.service.NotificationService;
import org.ssp.notification.service.ActiveMqSender;

import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class JobSchedular {

    @Autowired
    private NotificationService notificationServ;

    @Autowired
    private ActiveMqSender sender;

    int chunkSize = 100;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 120000, initialDelay = 60000)
    public void fetchData() {

        try {
            System.out.println("DB Job Started  " + (new Date()));
            while (true) {
                //System.out.println("Job Started with DBQuery " + (new Date()));
                // List<Notification> chunk = jdbcTemplate.queryForList("select id,recipient_email,sender_email,subject,body from Notification where status is null", Notification.class, chunkSize);
                List<Notification> chunk = jdbcTemplate.query(
                        "SELECT id,email_batch_id,ses_message_id, recipient_email,sender_email,subject,status,status_details,sent_timestamp,delivery_timestamp,bounce_timestamp,complaint_timestamp,error_code,body FROM Notification WHERE status IS NULL",
                        (rs, rowNum) -> new Notification(
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
                        )
                );
                if (chunk.isEmpty()) {
                    break;
                }
                processData(chunk);
            }

        } catch (Exception e) {
            System.out.println("Job Failed  " + new Date(System.currentTimeMillis()));
            e.printStackTrace();
        }
    }

    private void processData(List<Notification> chunk) {
        for (Notification notification : chunk) {
            NotificationDto notificationToSend = new NotificationDto().getNotificationDto(notification);
            notification.setStatus(Constant.PENDING_STS);
            notificationServ.saveNotification(notification);
            sender.sendMailBoxQ(notificationToSend);
        }
    }

}
