package org.ssp.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.ssp.notification.config.QueueConfig;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.dto.NotificationIdDto;

@Component
public class Sender {

    @Autowired
    private QueueConfig queueConfig;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMailBoxQ(NotificationDto notificationToSend) {
        jmsTemplate.convertAndSend(queueConfig.getMailboxQ(), notificationToSend);
    }

    public void sendMessageIdQ(NotificationIdDto notification) {
        jmsTemplate.convertAndSend(queueConfig.getMessageIdQ(), notification);
    }
}
