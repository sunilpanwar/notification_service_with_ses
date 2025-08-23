package org.ssp.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.dto.NotificationIdDto;

@Component
public class ActiveMqReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMqReceiver.class);

    private final SendEmailService sendEmail;
    private final NotificationService notificationService;
    private final long mailboxQDelay;

    public ActiveMqReceiver(SendEmailService sendEmail,
                            NotificationService notificationService,
                            @Value("${app.jms.listener.delay-ms:100}") long mailboxQDelay) {
        this.sendEmail = sendEmail;
        this.notificationService = notificationService;
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
        notificationService.updateMessageIdById(notification);
    }
}
