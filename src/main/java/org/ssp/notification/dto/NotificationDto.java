package org.ssp.notification.dto;

// Importing required classes

import lombok.*;
import org.ssp.notification.entity.Notification;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Class
public class NotificationDto{

    private Long id;
    private String recipientEmail;
    private String senderEmail;
    private String subject;
    private String body;

    public NotificationDto getNotificationDto (Notification notification) {
        return new NotificationDtoBuilder().id(notification.getId()).
                senderEmail(notification.getSenderEmail()).
                recipientEmail(notification.getRecipientEmail()).
                body(notification.getBody()).
                subject(notification.getSubject()).build();
    }
}