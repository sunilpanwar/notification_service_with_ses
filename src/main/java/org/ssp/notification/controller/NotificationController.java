package org.ssp.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.ssp.notification.Constant;
import org.ssp.notification.config.QueueConfig;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.entity.Notification;
import org.ssp.notification.service.NotificationServ;
import org.ssp.notification.service.Sender;

import java.util.Collection;
import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private NotificationServ notificationServ;

    @Autowired
    private Sender sender;

    @GetMapping("/get-all")
    public Collection<Notification> getAll() {
        return notificationServ.getNotificationRepo();
    }


    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Notification notification) {
        notificationServ.saveNotification(notification);
        return new ResponseEntity<>("saved successfully", HttpStatus.ACCEPTED);
    }
    @GetMapping("/send-message")
    public String sendMessage() {

        List<Notification> notifications = notificationServ.getNotificationByNullStatus();
        for (Notification notification : notifications) {
            NotificationDto notificationToSend = new NotificationDto().getNotificationDto(notification);
            sender.sendMailBoxQ(notificationToSend);
            notification.setStatus(Constant.PENDING_STS);
            notificationServ.saveNotification(notification);
        }
        return "successfully sent "+ notifications;
    }
}
