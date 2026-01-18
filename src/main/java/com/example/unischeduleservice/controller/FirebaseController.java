package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.NoticeFirebaseDTO;
import com.example.unischeduleservice.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/firebase")
@RequiredArgsConstructor
public class FirebaseController {
    private final FirebaseService firebaseService;

    @GetMapping
    public void sendMailFirebase(NoticeFirebaseDTO  noticeFirebaseDTO) {
        try {
            firebaseService.sendMessage(noticeFirebaseDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
