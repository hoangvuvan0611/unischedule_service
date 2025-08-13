package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.LoginRequest;
import com.example.unischeduleservice.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> loginSession(@RequestBody LoginRequest loginRequest) {
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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<String> getSessionValue(
            @RequestParam String username,
            @RequestParam String password) {

        String value = loginService.getSpecificSessionValue(username, password, "CURRENT_USER");
        return ResponseEntity.ok(value);
    }
}
