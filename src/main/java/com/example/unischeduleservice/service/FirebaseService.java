package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.NoticeFirebaseDTO;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

public interface FirebaseService {
    void sendMessage(NoticeFirebaseDTO noticeFirebaseDTO) throws FirebaseMessagingException;
    void sendBatchMessage(List<NoticeFirebaseDTO>  noticeFirebaseDTOs) throws FirebaseMessagingException;
}
