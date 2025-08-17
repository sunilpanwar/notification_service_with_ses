package org.ssp.notification.job;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ssp.notification.Constant;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.entity.Notification;
import org.ssp.notification.mapper.NotificationMapper;
import org.ssp.notification.mapper.NotificationRowMapper;
import org.ssp.notification.service.NotificationService;
import org.ssp.notification.service.ActiveMqSender;

import java.util.List;

@Component
@EnableScheduling
public class JobSchedular {

     /**
     * JobSchedular is responsible for periodically fetching pending notifications from the database: notification table,
     * updating their status, and sending them to a message queue for further processing.
     * It uses a fixed delay to run the job and processes notifications in chunks.
     */

    private static final String FETCH_PENDING_NOTIFICATIONS_SQL =
            "SELECT id, email_batch_id, ses_message_id, recipient_email, sender_email, subject, status, status_details, sent_timestamp, delivery_timestamp, bounce_timestamp, complaint_timestamp, error_code, body FROM notification WHERE status IS NULL LIMIT ?";

    private static final Logger logger = LoggerFactory.getLogger(JobSchedular.class);

    private final NotificationService notificationServ;
    private final ActiveMqSender sender;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationMapper notificationMapper;
    private final NotificationRowMapper notificationRowMapper;

    private final int chunkSize;

    // Using constructor injection is a best practice
    public JobSchedular(NotificationService notificationServ, ActiveMqSender sender,
                        JdbcTemplate jdbcTemplate, NotificationMapper notificationMapper, NotificationRowMapper notificationRowMapper,
                        @Value("${notification.job.chunk-size:100}") int chunkSize) {
        this.notificationServ = notificationServ;
        this.sender = sender;
        this.jdbcTemplate = jdbcTemplate;
        this.notificationMapper = notificationMapper;
        this.notificationRowMapper = notificationRowMapper;
        this.chunkSize = chunkSize;
    }

    @Scheduled(fixedDelayString = "${notification.job.fixed-delay:120000}", initialDelayString = "${notification.job.initial-delay:60000}")
    public void fetchData() {
        logger.info("Notification processing job started.");
        while (true) {
            try {
                List<Notification> chunk = jdbcTemplate.query(
                        FETCH_PENDING_NOTIFICATIONS_SQL,
                        notificationRowMapper,
                        chunkSize
                );

                if (chunk.isEmpty()) {
                    logger.info("No more pending notifications to process. Job finished.");
                    break;
                }
                logger.info("Fetched {} notifications to process.", chunk.size());
                processData(chunk);
            } catch (Exception e) {
                logger.error("Job failed during notification processing. Halting current run.", e);
                break;
            }
        }
    }

    private void processData(List<Notification> chunk) {
        // 1. Update the status on all objects in the list
        for (Notification notification : chunk) {
            notification.setStatus(Constant.PENDING_STS);
        }

        // 2. Save all changes in a single, efficient batch operation
        // This assumes NotificationServ has a method that calls repository.saveAll(chunk)
        notificationServ.saveAllNotifications(chunk);

        // 3. Send each corresponding DTO to the queue after the status is updated
        for (Notification notification : chunk) {
            NotificationDto notificationToSend = notificationMapper.toDto(notification);
            sender.sendMailBoxQ(notificationToSend);
        }
    }

}
