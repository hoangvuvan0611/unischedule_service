package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.LoginInfoDTO;
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

    @PostMapping(path = "/checkSendNotiFromAdminVnua")
    public void sendNotiFromAdminVnua(@RequestBody LoginInfoDTO loginInfoDTO) {
        notiService.sendMailNotiNewsFromAdminVnuaWithEachUser(loginInfoDTO);
    }
}
