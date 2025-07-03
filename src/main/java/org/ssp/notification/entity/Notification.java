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
    private String email_batch_id;
    private String ses_message_id;
    private String recipient_email;
    private String sender_email;
    private String subject;
    private String status;
    private String status_details;
    private Timestamp sent_timestamp;
    private Timestamp delivery_timestamp;
    private Timestamp bounce_timestamp;
    private Timestamp complaint_timestamp;
    private String error_code;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

}