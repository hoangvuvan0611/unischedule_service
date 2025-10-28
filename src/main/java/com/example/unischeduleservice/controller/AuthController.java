package com.example.unischeduleservice.controller;

import com.example.unischeduleservice.dto.LoginRequest;
import com.example.unischeduleservice.dto.LoginResponseDTO;
import com.example.unischeduleservice.dto.base.ResponseData;
import com.example.unischeduleservice.exceptions.CustomException;
import com.example.unischeduleservice.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> loginSession(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> sessionData = loginService
                    .loginAndGetSessionStorage(loginRequest.getUsername(), loginRequest.getPassword());

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

    @PostMapping("/loginAPI")
    public ResponseData<?> testLogin(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponseDTO response = loginService.loginWithAPI(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseData.success("success", response);
        } catch (CustomException ce) {
            logger.error(ce.getMessage(), ce);
            return ResponseData.error(-1, ce.getMessage());
        }
    }
}
