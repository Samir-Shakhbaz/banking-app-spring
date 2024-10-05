//package com.sash.banking_app_spring.services;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TwilioSmsService {
//
//    @Value("${twilio.account.sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth.token}")
//    private String authToken;
//
//    @Value("${twilio.phone.number}")
//    private String twilioPhoneNumber;
//
//    public TwilioSmsService() {
//        Twilio.init(accountSid, authToken);
//    }
//
//    public void sendSms(String to, String message) {
//        Message.creator(
//                new PhoneNumber(to),   // To phone number
//                new PhoneNumber(twilioPhoneNumber),  // From Twilio number
//                message).create();
//    }
//}