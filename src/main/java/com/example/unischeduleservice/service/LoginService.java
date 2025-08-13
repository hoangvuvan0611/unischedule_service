package com.example.unischeduleservice.service;

import java.util.Map;

public interface LoginService {
    Map<String, String> loginAndGetSessionStorage(String username, String password);
    String getSpecificSessionValue(String username, String password, String key);
}
