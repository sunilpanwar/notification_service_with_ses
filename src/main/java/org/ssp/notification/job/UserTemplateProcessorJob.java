package org.ssp.notification.job;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.ssp.notification.entity.Notification;
import org.ssp.notification.entity.Template;
import org.ssp.notification.entity.UserData;
import org.ssp.notification.service.NotificationService;
import org.ssp.notification.service.TemplateService;
import org.ssp.notification.service.UserDataService;

import java.util.List;

@Configuration
@EnableScheduling
public class UserTemplateProcessorJob {

    private static final Logger log = LoggerFactory.getLogger(UserTemplateProcessorJob.class);

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private NotificationService notificationServ;

    @Autowired
    private TemplateService templateService;


    int chunkSize = 50;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelayString = "${job.user-processor.fixed-delay}", initialDelayString = "${job.user-processor.initial-delay}")
    public void fetchData() {
        log.info("Starting UserTemplateProcessorJob to process user data and create notifications.");

        Template template = templateService.findByStatus(true);
        if (template == null) {
            log.warn("No active template found. Skipping this job run.");
            return;
        }

        /*userDataService.getUserDataByReportedComplainFalse() this will return list of user,
         templateService.findByStatus(true)) THIS WILL RETURN ONE TEMPLATE WHICH IS ACTIVE
        so we can use this template to send email to all users AND REPLACE THE PLACEHOLDER WITH USER DATA <Name>
         and insert into the Notification table and update the userData table with modified_at  as current time.
        */
        // Example of processing user data with template


        List<UserData> userDataList = userDataService.getUserDataByReportedComplainFalse();
        if (userDataList.isEmpty()) {
            log.info("No user data found to process.");
            return;
        }
        log.info("Found {} users to notify. Processing a chunk of {}.", userDataList.size(), chunkSize);

        List<Integer> userIds = new java.util.ArrayList<>();
        List<Notification> notifications = userDataList.stream().limit(chunkSize).
                map(userData -> {
                    userIds.add(userData.getId());
                    String body = template.getMessage()
                            .replace("{Name}", userData.getName());

                    return new Notification().builder().body(body)
                            .sender_email(template.getSenderEmail())
                            .recipient_email(userData.getEmail())
                            .subject(template.getSubject()).build();

                }).toList();

        log.info("Generated {} notifications. Saving them to the database.", notifications.size());
        //update userData with userIds
        userDataService.updateProcessStatusToCompleted(userIds);
        notificationServ.saveAllNotifications(notifications);
        log.info("Successfully saved notifications and updated user statuses.");
    }
}