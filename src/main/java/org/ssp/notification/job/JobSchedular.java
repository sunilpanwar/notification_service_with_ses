package org.ssp.notification.job;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.ssp.notification.Constant;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.entity.InitialMessage;
import org.ssp.notification.entity.Notification;
import org.ssp.notification.repository.InitialMessageRepository;
import org.ssp.notification.service.NotificationService;
import org.ssp.notification.service.ActiveMqSender;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableScheduling
public class JobSchedular {

    private static final Logger log = LoggerFactory.getLogger(JobSchedular.class);

    @Autowired
    private NotificationService notificationServ;

    @Autowired
    private ActiveMqSender sender;

    @Autowired
    private InitialMessageRepository initialMessageRepository;

    private final int chunkSize = 100;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelayString = "${job.db-polling.fixed-delay}", initialDelayString = "${job.db-polling.initial-delay}")
    public void fetchData() {
        log.info("DB Polling Job Started.");
        int processedCount;
        do {
            processedCount = 0;
            try {
                // Fetch a limited chunk of notifications to avoid memory issues and long transactions.
                // NOTE: 'LIMIT ?' is for MySQL/PostgreSQL. Use 'SELECT TOP (?)' for SQL Server or 'FETCH FIRST ? ROWS ONLY' for Oracle.
                List<Notification> chunk = jdbcTemplate.query(
                        "SELECT id,email_batch_id,ses_message_id, recipient_email,sender_email,subject,status,status_details,sent_timestamp,delivery_timestamp,bounce_timestamp,complaint_timestamp,error_code,body FROM Notification WHERE status IS NULL LIMIT ?",
                        ps -> ps.setInt(1, chunkSize),
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

                if (!chunk.isEmpty()) {
                    log.info("Found {} notifications to process.", chunk.size());
                    processData(chunk);
                    processedCount = chunk.size();
                } else {
                    log.debug("No pending notifications found in this cycle.");
                }
            } catch (Exception e) {
                log.error("Job failed while processing a chunk. Aborting for this schedule.", e);
                break; // Exit loop on error to prevent repeated failures
            }
        } while (processedCount == chunkSize); // Continue if the last chunk was full
        log.info("DB Polling Job Finished.");
    }

    private void processData(List<Notification> chunk) {
        for (Notification notification : chunk) {
            String initialMessageId = findInitialMessageId(notification.getSender_email(), notification.getRecipient_email());

            NotificationDto notificationToSend = NotificationDto.builder()
                    .id(notification.getId())
                    .sender_email(notification.getSender_email())
                    .recipient_email(notification.getRecipient_email())
                    .subject(notification.getSubject())
                    .body(notification.getBody())
                    .inReplyToMessageId(initialMessageId) // Pass the ID to the DTO
                    .batch_id(notification.getEmail_batch_id())
                    .build();

            notification.setStatus(Constant.PENDING_STS);
            notificationServ.saveNotification(notification);
            sender.sendMailBoxQ(notificationToSend);
        }
    }

    /**
     * Finds the initial message ID by looking in the pre-populated, permanent
     * 'notification_initial_message_id' table. This is very fast and avoids locking the main Notification table.
     */
    private String findInitialMessageId(String senderEmail, String recipientEmail) {
        if (senderEmail == null || recipientEmail == null) {
            return null;
        }

        // Query the fast, small, permanent lookup table.
        Optional<InitialMessage> lookupResult =
                initialMessageRepository.findBySenderEmailAndRecipientEmail(senderEmail, recipientEmail);

        // If a result is found, return the message ID. Otherwise, return null.
        return lookupResult.map(InitialMessage::getMessageId).orElse(null);
    }
}
