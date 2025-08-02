package org.ssp.notification.dto;

// Importing required classes

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ssp.notification.entity.Notification;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Class
public class NotificationDto{

    private Long id;
    private String recipient_email;
    private String sender_email;
    private String subject;
    private String body;

    public NotificationDto getNotificationDto (Notification notification) {
        return new NotificationDtoBuilder().id(notification.getId()).
                sender_email(notification.getSender_email()).
                recipient_email(notification.getRecipient_email()).
                body(notification.getBody()).
                subject(notification.getSubject()).build();
    }
}