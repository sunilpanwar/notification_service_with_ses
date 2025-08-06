package org.ssp.notification.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.ssp.notification.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Integer> {
    // Additional query methods can be defined here :List<UserData> findByNonReportedComplain
    //write custome query here

    @Query("SELECT u FROM UserData u WHERE u.reportedComplain = false and u.process_status = 1")
    List<UserData> findByReportedComplainFalse();


    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("UPDATE UserData u SET u.process_status = 2 WHERE u.id IN :ids")
    void updateProcessStatusToCompleted(List<Integer> ids);

}