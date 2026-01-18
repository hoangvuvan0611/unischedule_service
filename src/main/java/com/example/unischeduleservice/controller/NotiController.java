package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.LoginRequest;
import com.example.unischeduleservice.service.NotiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/noti")
@RequiredArgsConstructor
public class NotiController {
    private final NotiService notiService;

    @PostMapping(path = "/sendNotiFromAdminVnua")
    public void sendNotiFromAdminVnua(@RequestBody LoginRequest loginRequest) {
        notiService.sendMailNotiNewsFromAdminVnuaWithEachUser(loginRequest);
    }
}
