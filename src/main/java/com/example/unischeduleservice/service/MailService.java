package com.example.unischeduleservice.service;

public interface MailService {

    void sendMail(String to, String subject, String text);
}
