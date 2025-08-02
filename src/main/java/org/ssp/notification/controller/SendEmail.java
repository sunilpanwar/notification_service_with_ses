package org.ssp.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssp.notification.config.AwsConfig;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.dto.NotificationIdDto;
import org.ssp.notification.service.NotificationServ;
import org.ssp.notification.service.Sender;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import static org.ssp.notification.Constant.*;

@Service
public class SendEmail {

    @Autowired
    private NotificationServ notificationServ;

    @Autowired
    private Sender sender;

    @Autowired
    private AwsConfig awsConfig;
   /* final String usage = """

                                Usage:
                                    <sender> <recipient> <subject>\s

                                Where:
                                    sender - An email address that represents the sender.\s
                                    recipient - An email address that represents the recipient.\s
                                    subject - The subject line.\s
                                """;


    String mailSender = "sunil_panwar@outlook.com";
    String mailReceiver = "sspneel@gmail.com";
    String subject = "Test Notification Service";

    // The HTML body of the email.
    String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                + "<p> See the list of customers.</p>" + "</body>" + "</html>";*/

    public  void send(NotificationDto notification) {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsConfig.getAccessKeyId(), awsConfig.getSecretAccessKey());
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        Region region = Region.of(awsConfig.getRegion());
        SesV2Client client = SesV2Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        Destination destination = Destination.builder()
                .toAddresses(notification.getRecipient_email())
                .build();

        Content content = Content.builder()
                .data(notification.getBody())
                .build();

        Content sub = Content.builder()
                .data(notification.getSubject())
                .build();

        Body body = Body.builder()
                .html(content)
                .build();

        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();

        EmailContent emailContent = EmailContent.builder()
                .simple(msg)
                .build();

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .content(emailContent)
                .fromEmailAddress(notification.getSender_email())
                .build();

        NotificationIdDto messageIdDto = NotificationIdDto.builder().
                id(notification.getId()).
                status(SENT_STS).
                status_details(SUCCESS_STS).
                build();
        try {
            System.out.println("Attempting to send an email through Amazon SES "
                    + "using the AWS SDK for Java...");
            SendEmailResponse sendEmailResponse = client.sendEmail(emailRequest);

            System.out.println(emailRequest + " : email was sent " + sendEmailResponse.messageId());
            //notificationServ.saveNotification();
            //insert into the Queue to update the messageId in the DB table.

            messageIdDto.setMessageId(sendEmailResponse.messageId());
        } catch (SesV2Exception e)  {
            System.err.println("Error During sending the message => " + e.awsErrorDetails().errorMessage());
            messageIdDto.setStatus(FAILED_STS);
            messageIdDto.setStatus_details(e.awsErrorDetails().errorMessage());
        }
        finally {
            sender.sendMessageIdQ( messageIdDto);
        }

    }
}