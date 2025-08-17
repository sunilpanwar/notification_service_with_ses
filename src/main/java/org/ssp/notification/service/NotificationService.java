package org.ssp.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssp.notification.dto.NotificationIdDto;
import org.ssp.notification.entity.Notification;
import org.ssp.notification.repository.NotificationRepo;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    Logger logger = LoggerFactory.getLogger(NotificationService.class);
    public void saveNotification(Notification notification) {
        notificationRepo.save(notification);

    }

    public List<Notification> getNotificationRepo() {
        return notificationRepo.findAll();
    }

    public List<Notification> getNotificationByNullStatus() {
        return notificationRepo.findAllByNull();
    }

    public void updateMessageIdById(NotificationIdDto notification) {
        notificationRepo.updateMessageIdById(notification.getId(), notification.getMessageId(),
                notification.getStatus(), notification.getStatus_details());
        logger.info("****** Updated SUccessfully !! " + notification);
    }


    public void saveAllNotifications(List<Notification> notifications) {
        notificationRepo.saveAll(notifications);
    }
}
