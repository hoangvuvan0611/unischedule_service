package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.NoticeFirebaseDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface FirebaseService {
    void sendMessage(NoticeFirebaseDTO noticeFirebaseDTO) throws FirebaseMessagingException;
}
