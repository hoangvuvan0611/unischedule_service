package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.NoticeFirebaseDTO;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseServiceImpl implements FirebaseService {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseServiceImpl.class);

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendMessage(NoticeFirebaseDTO noticeFirebaseDTO) {
        String token = noticeFirebaseDTO.getRegistrationTokens().getFirst();
        String subject = noticeFirebaseDTO.getSubject();
        String body = noticeFirebaseDTO.getContent();
        String image = noticeFirebaseDTO.getImage();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(subject)
                        .setBody(body)
                        .setImage(image)
                        .build())
                .putAllData(noticeFirebaseDTO.getData())
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}