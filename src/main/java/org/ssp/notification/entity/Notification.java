package org.ssp.notification.entity;

// Importing required classes
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ssp.notification.config.AuditableEntity;

import java.sql.Timestamp;


// Annotations
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// Class
public class Notification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_batch_id")
    private String emailBatchId;

    @Column(name = "ses_message_id")
    private String sesMessageId;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "sender_email")
    private String senderEmail;
    private String subject;
    private String status;

    @Column(name = "status_details")
    private String statusDetails;

    @Column(name = "sent_timestamp", columnDefinition = "DATETIME(6)")
    private Timestamp sentTimestamp;

    @Column(name = "delivery_timestamp", columnDefinition = "DATETIME(6)")
    private Timestamp deliveryTimestamp;

    @Column(name = "bounce_timestamp", columnDefinition = "DATETIME(6)")
    private Timestamp bounceTimestamp;

    @Column(name = "complaint_timestamp", columnDefinition = "DATETIME(6)")
    private Timestamp complaintTimestamp;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

}