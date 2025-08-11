package org.ssp.notification.service;


import org.ssp.notification.entity.UserData;
import org.ssp.notification.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDataService {

    private final UserDataRepository userDataRepository;

    @Autowired
    public UserDataService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    public List<UserData> getAllUserData() {
        return userDataRepository.findAll();
    }

    public Optional<UserData> getUserDataById(Integer id) {
        return userDataRepository.findById(id);
    }

    public UserData saveUserData(UserData userData) {
        return userDataRepository.save(userData);
    }

    public void deleteUserDataById(Integer id) {
        userDataRepository.deleteById(id);
    }

    public List<UserData> getUserDataByReportedComplainFalse() {
        return userDataRepository.findByReportedComplainFalse();
    }


    public void updateProcessStatusToCompleted(List<Integer> ids) {
        userDataRepository.updateProcessStatusToCompleted(ids);
    }
}