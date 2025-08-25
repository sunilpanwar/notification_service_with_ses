package org.ssp.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssp.notification.entity.InitialMessage;

import java.util.Optional;

public interface InitialMessageRepository extends JpaRepository<InitialMessage, Long> {

    Optional<InitialMessage> findBySenderEmailAndRecipientEmail(String senderEmail, String recipientEmail);
}