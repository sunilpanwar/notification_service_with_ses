package org.ssp.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_status")
    private String deliveryStatus;

    @Column(name = "status_details")
    private String statusDetails;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    @Column(name = "sent_timestamp", columnDefinition = "DATETIME(6)")
    private LocalDateTime sentTimestamp;

    @Column(name = "delivery_timestamp", columnDefinition = "DATETIME(6)")
    private LocalDateTime deliveryTimestamp;

    @Column(name = "bounce_timestamp", columnDefinition = "DATETIME(6)")
    private LocalDateTime bounceTimestamp;

    @Column(name = "complaint_timestamp", columnDefinition = "DATETIME(6)")
    private LocalDateTime complaintTimestamp;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "email_batch_id")
    private String emailBatchId;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "ses_message_id")
    private String sesMessageId;

  /*  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;*/

    // Getters and setters
}

