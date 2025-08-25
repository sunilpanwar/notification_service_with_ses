package org.ssp.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssp.notification.config.AwsConfig;
import org.ssp.notification.dto.NotificationDto;
import org.ssp.notification.dto.NotificationIdDto;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;
import software.amazon.awssdk.core.SdkBytes;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.ssp.notification.Constant.*;

@Service
public class SendEmailService {

    private static final Logger log = LoggerFactory.getLogger(SendEmailService.class);

    @Autowired
    private NotificationService notificationServ;

    @Autowired
    private ActiveMqSender sender;

    @Autowired
    private AwsConfig awsConfig;

    public  void send(NotificationDto notification) {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsConfig.getAccessKeyId(), awsConfig.getSecretAccessKey());
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        Region region = Region.of(awsConfig.getRegion());
        SesV2Client client = SesV2Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        NotificationIdDto messageIdDto = NotificationIdDto.builder().
                id(notification.getId()).
                status(SENT_STS).
                status_details(SUCCESS_STS).
                build();
        try {
            // To add custom headers like 'In-Reply-To', we must build a raw email message.
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);

            // Set standard headers
            message.setFrom(new InternetAddress(notification.getSender_email()));
            message.setRecipients(RecipientType.TO, InternetAddress.parse(notification.getRecipient_email()));
            message.setSubject(notification.getSubject());

            // If an In-Reply-To ID is provided, add the necessary headers for email threading.
            if (notification.getInReplyToMessageId() != null && !notification.getInReplyToMessageId().isBlank()) {
                log.debug("Adding In-Reply-To header for notification ID {}: {}", notification.getId(), notification.getInReplyToMessageId());
                message.addHeader("In-Reply-To", notification.getInReplyToMessageId());
                message.addHeader("References", notification.getInReplyToMessageId());
            }

            // Create the HTML body part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(notification.getBody(), "text/html; charset=UTF-8");

            // Create a multipart message and add the HTML part
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);

            // Write the message to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);

            // Build the RawMessage for SES
            RawMessage rawMessage = RawMessage.builder()
                    .data(SdkBytes.fromByteArray(outputStream.toByteArray()))
                    .build();

            EmailContent emailContent = EmailContent.builder()
                    .raw(rawMessage)
                    .build();

            // The destination is part of the raw message headers, but SES still requires it here.
            Destination destination = Destination.builder()
                    .toAddresses(notification.getRecipient_email())
                    .build();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(destination)
                    .content(emailContent)
                    .fromEmailAddress(notification.getSender_email()) // The 'From' address must be verified in SES.
                    .build();

            log.info("Attempting to send an email for notification ID {} through Amazon SES...", notification.getId());
            SendEmailResponse sendEmailResponse = client.sendEmail(emailRequest);

            log.info("Email for notification ID {} was sent successfully. Message ID: {}", notification.getId(), sendEmailResponse.messageId());
            messageIdDto.setMessageId(sendEmailResponse.messageId());
        } catch (IOException | MessagingException | SesV2Exception e)  {
            String errorMessage = (e instanceof SesV2Exception sesEx)
                    ? sesEx.awsErrorDetails().errorMessage()
                    : e.getMessage();

            log.error("Failed to send email for notification ID {}: {}", notification.getId(), errorMessage, e);
            messageIdDto.setStatus(FAILED_STS);
            messageIdDto.setStatus_details(errorMessage);
        }
        finally {
            sender.sendMessageIdQ( messageIdDto);
        }

    }
}