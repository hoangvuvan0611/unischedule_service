package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.DeviceDTO;

public interface DeviceService {
    void checkAndSaveDevice(DeviceDTO device);
    void checkAndDeleteDevice(DeviceDTO device);
}
