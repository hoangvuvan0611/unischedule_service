package com.example.unischeduleservice.service;

import com.example.unischeduleservice.dto.DeviceDTO;
import com.example.unischeduleservice.models.Device;
import com.example.unischeduleservice.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    private final DeviceRepository deviceRepository;

    @Override
    public void checkAndSaveDevice(DeviceDTO device) {
        if (!StringUtils.hasText(device.getId())) {
            return;
        }

        // Kiem tra xem token nay da ton tai chua
        boolean deviceIsExists = deviceRepository.existsById(device.getId());
        // Neu chua tung luu thi thuc hien luu moi
        if (!deviceIsExists) {
            deviceRepository.save(Device.builder()
                    .username(device.getUsername())
                    .password(device.getPassword())
                    .os(device.getOs())
                    .deviceName(device.getDeviceName())
                    .id(device.getId())
                    .createdAt(LocalDateTime.now())
                    .build());
        } else {
            Device deviceObject = deviceRepository.findById(device.getId()).orElse(null);
            // Neu da ton tai vao username, password khong thay doi thi khong thuc hien gi ca
            if (deviceObject == null || (deviceObject.getUsername().equals(device.getUsername())
                    && deviceObject.getPassword().equals(device.getPassword()))) {
                return;
            }
            // Cap nhat lai neu thong tin username hoac password thay doi
            deviceObject.setUsername(device.getUsername());
            deviceObject.setPassword(device.getPassword());
            deviceRepository.save(deviceObject);
        }
    }

    @Override
    public void checkAndDeleteDevice(DeviceDTO device) {
        if (!StringUtils.hasText(device.getId())) {
            return;
        }
        deviceRepository.deleteById(device.getId());
    }

    @Override
    public List<Device> getAll() {
        return deviceRepository.findAll();
    }
}
