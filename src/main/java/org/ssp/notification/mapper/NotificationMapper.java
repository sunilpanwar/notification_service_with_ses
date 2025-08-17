package org.ssp.notification.mapper;

import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }


        return NotificationDto.builder()
                .id(notification.getId())
                .recipientEmail(notification.getRecipientEmail())
                .senderEmail(notification.getSenderEmail())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .build();
    }
}