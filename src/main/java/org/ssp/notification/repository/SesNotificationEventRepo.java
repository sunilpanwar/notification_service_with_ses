package org.ssp.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ssp.notification.entity.SesNotificationEventType;

@Repository
public interface SesNotificationEventRepo extends JpaRepository<SesNotificationEventType, Integer> {

}
