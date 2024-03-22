package com.example.mobilnetestiranjebackend.helpers;


import com.twilio.Twilio;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serial;

@Service
public class TwilioService {

    private final String ACCOUNT_SID;
    private final String AUTH_TOKEN;

    @Getter
    private final com.twilio.rest.verify.v2.Service service;


    @Autowired
    public TwilioService(@Value("${twilio.accountSid}") String accountSid,
                         @Value("${twilio.authToken}") String authToken) {
        this.ACCOUNT_SID = accountSid;
        this.AUTH_TOKEN = authToken;
        Twilio.init(accountSid, authToken);


        this.service = com.twilio.rest.verify.v2.Service
                .creator("Phone service")
                .create();
    }


}
