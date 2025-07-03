package org.ssp.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "template")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "subject", nullable = false, length = 1000)
    private String subject;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "status", columnDefinition = "BINARY(1)")
    private byte[] status;

    @Column(name = "starttime")
    private LocalDateTime startTime;

    @Column(name = "endtime")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notifications> notifications;

    // Getters and setters
}
