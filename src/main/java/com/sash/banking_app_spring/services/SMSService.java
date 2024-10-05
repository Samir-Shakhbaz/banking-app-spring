package com.sash.banking_app_spring.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SMSService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @Value("${sms.enabled:false}")
    private boolean smsEnabled;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendNotification(String toPhoneNumber, String messageBody) {
        if (!smsEnabled) {
            System.out.println("SMS notifications are disabled.");
            return;
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),   // To phone number
                    new PhoneNumber(fromPhoneNumber), // From Twilio number
                    messageBody
            ).create();

            System.out.println("SMS sent successfully: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send SMS notification");
        }
    }
}

