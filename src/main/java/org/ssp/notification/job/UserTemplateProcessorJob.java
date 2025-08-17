package org.ssp.notification.job;


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

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private NotificationService notificationServ;

    @Autowired
    private TemplateService templateService;

    int chunkSize = 50;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 30000, initialDelay = 1000)
    public void fetchData() {

        Template template = templateService.findByStatus(true);

        /*userDataService.getUserDataByReportedComplainFalse() this will return list of user,
         templateService.findByStatus(true)) THIS WILL RETURN ONE TEMPLATE WHICH IS ACTIVE
        so we can use this template to send email to all users AND REPLACE THE PLACEHOLDER WITH USER DATA <Name>
         and insert into the Notification table and update the userData table with modified_at  as current time.
        */
        // Example of processing user data with template

//getUserDataByReportedComplainFalse use chunkz or size while fetching data.

        List<UserData> userDataList = userDataService.getUserDataByReportedComplainFalse();
        if (userDataList.isEmpty()) {
            System.out.println("No user data found to process.");
            return;
        }
        List<Integer> userIds = new java.util.ArrayList<>();
        List<Notification> notifications = userDataList.stream().limit(chunkSize).
                map(userData -> {
                    userIds.add(userData.getId());
                    String body = template.getMessage()
                            .replace("{Name}", userData.getName());

                    return new Notification().builder().body(body)
                            .senderEmail(template.getSenderEmail())
                            .recipientEmail(userData.getEmail())
                            .subject(template.getSubject()).build();

                }).toList();
//update userData with userIds
        userDataService.updateProcessStatusToCompleted(userIds);
        notificationServ.saveAllNotifications(notifications);

    }
}