package org.ssp.notification.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class MessageReceiver
{

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    private final String queueName = "email_notification_queue";

    @Autowired
    private SqsTemplate sqsTemplate;

    public void pollMessages()
    {
        LOGGER.info("Inside pollMessages** : "+ (new Date()));
        while (true)
        {
            Optional<Message<?>> optionalMessage = sqsTemplate.receive(from -> from.queue(queueName));
            if(optionalMessage!=null && optionalMessage.isPresent())
            {
                Message<?> message = optionalMessage.get();
                LOGGER.info(message.getPayload() + " = received on listen method at {}", OffsetDateTime.now());
            }
            else
            {
                LOGGER.info("No message received: "+ OffsetDateTime.now());
            }
        }

    }
}