package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.LoginResponseDTO;

import java.util.Map;

public interface LoginService {
    Map<String, String> loginAndGetSessionStorage(String username, String password);
    String getSpecificSessionValue(String username, String password, String key);
    LoginResponseDTO loginWithAPI(String username, String password);
}
