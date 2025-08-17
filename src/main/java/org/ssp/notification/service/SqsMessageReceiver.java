package org.ssp.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.ssp.notification.dto.SqsMessageMapper;
import org.ssp.notification.dto.SqsRootMapper;
import org.ssp.notification.entity.SesNotificationEventType;
import org.ssp.notification.repository.SesNotificationEventRepo;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class SqsMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqsMessageReceiver.class);

    private final String queueName = "email_notification_queue";

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private SesNotificationEventRepo sesNotificationEventRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void pollMessages() {
        LOGGER.info("Inside pollMessages** : " + (new Date()));
        while (true) {
            Optional<Message<?>> optionalMessage = sqsTemplate.receive(from -> from.queue(queueName));
            if (optionalMessage != null && optionalMessage.isPresent()) {
                Message<?> message = optionalMessage.get();
                LOGGER.info("###: " + message.getPayload() + " = received on listen method at {}", OffsetDateTime.now());
                try {
                    if (message.getPayload().toString().contains("eventType")) {
                        SqsRootMapper root = objectMapper.readValue(message.getPayload().toString(), SqsRootMapper.class);
                        SqsMessageMapper messageMpr = objectMapper.readValue(root.getMessage(), SqsMessageMapper.class);

                        LOGGER.debug("EventType: {} and MessageId {}", messageMpr.getEventType(), messageMpr.getMail().getMessageId());
                        SesNotificationEventType sesNotificationEventType = SesNotificationEventType.builder()
                                .eventType(messageMpr.getEventType())
                                .messageId(messageMpr.getMail().getMessageId())
                                .emailId(messageMpr.getMail().getDestination().get(0))
                                .build();
                        sesNotificationEventRepo.save(sesNotificationEventType);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("else No message received: " + OffsetDateTime.now());
            }

        }

    }
}