package org.ssp.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssp.notification.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    @Override
    List<Notification> findAll();

    @Query("select p from Notification p where p.status is null")
    List<Notification> findAllByNull();
    //List<Department> findByDepartmentName(String departmentName);

    @Override
    <S extends Notification> S save(S entity);

    @Modifying
    @Transactional
    @Query("""
    update Notification p set p.ses_message_id = :ses_message_id, p.status = :status,
     p.status_details = :status_details where p.id = :id""")
    void updateMessageIdById(@Param("id") Long id, @Param("ses_message_id") String ses_message_id,
                             @Param("status") String status, @Param("status_details") String statusDetails);

}
