package org.ssp.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.ssp.notification.controller.SendEmail;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.dto.NotificationIdDto;

@Component
public class ActiveMqReceiver {

    @Autowired
    private SendEmail sendEmail;

    @Autowired
    private NotificationService notificationServ;

    @JmsListener(destination = "${activemq.mailboxQ}", containerFactory = "myFactory")
    public void receiveMailboxQ(NotificationDto notification) throws InterruptedException {
        System.out.println("Received from mailboxQ: <" + notification + ">");
        sendEmail.send(notification);
        Thread.sleep(1000);
    }

    @JmsListener(destination = "${activemq.messageIdQ}", containerFactory = "myFactory")
    public void receiveMessageIdQ(NotificationIdDto notification) throws InterruptedException {
        System.out.println("Received from: messageIdQ: <" + notification + ">");
        notificationServ.updateMessageIdById(notification);
    }
}

