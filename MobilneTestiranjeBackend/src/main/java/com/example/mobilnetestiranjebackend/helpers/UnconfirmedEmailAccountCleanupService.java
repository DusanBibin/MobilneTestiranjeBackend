package com.example.mobilnetestiranjebackend.helpers;


import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UnconfirmedEmailAccountCleanupService {
    @Autowired
    private UserRepository userRepository;


    @Scheduled(cron = "0 * * * * *") // Execute every minute
    public void deleteAccountsWithUnconfirmedEmails() {
        var unconfirmedUsers = userRepository.findByEmailNotConfirmedAndExpired();

        if(!unconfirmedUsers.isEmpty()) userRepository.deleteAll(unconfirmedUsers);
    }
}
