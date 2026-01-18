package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.DeviceDTO;
import com.example.unischeduleservice.models.Device;

import java.util.List;

public interface DeviceService {
    void checkAndSaveDevice(DeviceDTO device);
    void checkAndDeleteDevice(DeviceDTO device);
    List<Device> getAll();
}
