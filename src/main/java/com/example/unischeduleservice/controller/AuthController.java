package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.LoginRequest;
import com.example.unischeduleservice.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public String loginSession(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, String> sessionData = loginService
                    .loginAndGetSessionStorage("6656485", "Hoanglam06112003");

            if (!sessionData.isEmpty()) {
                response.put("success", true);
                response.put("sessionData", sessionData);
                response.put("message", "Đăng nhập thành công");
            } else {
                response.put("success", false);
                response.put("message", "Đăng nhập thất bại");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }
        return null;
    }
}
