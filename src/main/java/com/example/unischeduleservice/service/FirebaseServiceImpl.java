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
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FirebaseServiceImpl implements FirebaseService {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseServiceImpl.class);

    private final ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;

    @Override
    public void sendMessage(NoticeFirebaseDTO noticeFirebaseDTO) {
        FirebaseMessaging firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
        if (firebaseMessaging == null) {
            logger.warn("FirebaseMessaging bean is not available, skipping push notification");
            return;
        }
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

    @Override
    public void sendBatchMessage(List<NoticeFirebaseDTO> noticeFirebaseDTOs) throws FirebaseMessagingException {

    }
}
