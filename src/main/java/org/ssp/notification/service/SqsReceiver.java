package org.ssp.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssp.notification.config.AwsConfig;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Date;
import java.util.List;

//@Service
public class SqsReceiver {

    @Autowired
    private AwsConfig awsConfig;

    public void receiveFromSqs() {
        try (SqsClient sqsClient = SqsClient.create()) {
            while (true) {
                System.out.println("inside receiveFromSqs() :" + (new Date()));
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(awsConfig.getSqslUrl())
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(10)
                        .build();

                List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

                for (Message message : messages) {
                    System.out.println("Received message:------");
                   // System.out. println( message.messageAttributes().get("Message"));
                  //  System.out.println(message.body());

                    // Optionally parse JSON and extract fields

                    // Delete message after processing
                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(awsConfig.getSqslUrl())
                            .receiptHandle(message.receiptHandle())
                            .build();

                    sqsClient.deleteMessage(deleteRequest);
                }
            }
        }
    }
}
