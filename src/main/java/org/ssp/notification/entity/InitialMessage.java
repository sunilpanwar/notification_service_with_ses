package org.ssp.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "notification_initial_message_id",
    // This unique constraint is crucial to prevent duplicate entries
    uniqueConstraints = @UniqueConstraint(columnNames = {"sender_email", "recipient_email"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitialMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "message_id", nullable = false)
    private String messageId;
}