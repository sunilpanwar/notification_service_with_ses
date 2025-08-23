package org.ssp.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.ssp.notification.controller.SendEmail;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.dto.NotificationIdDto;

@Component
public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private final SendEmail sendEmail;
    private final NotificationServ notificationServ;
    private final long mailboxQDelay;

    public Receiver(SendEmail sendEmail,
                    NotificationServ notificationServ,
                    @Value("${app.jms.listener.delay-ms:100}") long mailboxQDelay) {
        this.sendEmail = sendEmail;
        this.notificationServ = notificationServ;
        this.mailboxQDelay = mailboxQDelay;
    }

    @JmsListener(destination = "${activemq.mailboxQ}", containerFactory = "myFactory")
    public void receiveMailboxQ(NotificationDto notification) throws InterruptedException {
        logger.info("Received from mailboxQ: <{}>", notification);
        sendEmail.send(notification);
        // Using the delay injected from application.properties
        Thread.sleep(mailboxQDelay);
    }

    @JmsListener(destination = "${activemq.messageIdQ}", containerFactory = "myFactory")
    public void receiveMessageIdQ(NotificationIdDto notification) {
        logger.info("Received from messageIdQ: <{}>", notification);
        notificationServ.updateMessageIdById(notification);
    }
}
