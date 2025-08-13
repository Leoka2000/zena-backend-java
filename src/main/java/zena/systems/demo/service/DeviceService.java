package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.DeviceRequestDto;
import zena.systems.demo.dto.DeviceResponseDto;
import zena.systems.demo.dto.DeviceUpdateRequestDto;
import zena.systems.demo.model.*;
import zena.systems.demo.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final TemperatureRepository temperatureRepository;
    private final AccelerometerRepository accelerometerRepository;
    private final VoltageRepository voltageRepository;

    @Transactional
    public DeviceResponseDto createDevice(DeviceRequestDto dto) {
        AppUser currentUser = getCurrentUser();
        Device device = new Device();
        device.setServiceUuid(dto.getServiceUuid());
        device.setReadNotifyCharacteristicUuid(dto.getReadNotifyCharacteristicUuid());
        device.setWriteCharacteristicUuid(dto.getWriteCharacteristicUuid());
        device.setName(dto.getName());
        device.setUser(currentUser);

        Device saved = deviceRepository.save(device);

        // Create a new Temperature instance tied to device and user
        Temperature temp = new Temperature();
        temp.setTemperature(0.0f);
        temp.setTimestamp(System.currentTimeMillis() / 1000);
        temp.setDevice(saved);
        temp.setUser(currentUser);
        temperatureRepository.save(temp);

        // Create a new Accelerometer instance tied to device and user
        Accelerometer accel = new Accelerometer();
        accel.setX(0.0f);
        accel.setY(0.0f);
        accel.setZ(0.0f);
        accel.setTimestamp(System.currentTimeMillis() / 1000);
        accel.setDevice(saved);
        accel.setUser(currentUser);
        accelerometerRepository.save(accel);

        // Create a new Voltage instance tied to device and user
        Voltage voltage = new Voltage();
        voltage.setVoltage(0.0f);
        voltage.setTimestamp(System.currentTimeMillis() / 1000);
        voltage.setDevice(saved);
        voltage.setUser(currentUser);
        voltageRepository.save(voltage);

        // Update user hasCreatedFirstDevice flag if needed
        if (!currentUser.isHasCreatedFirstDevice()) {
            currentUser.setHasCreatedFirstDevice(true);
            userRepository.save(currentUser);
        }

        return mapToDto(saved);
    }

    public List<DeviceResponseDto> getUserDevices() {
        AppUser currentUser = getCurrentUser();
        return deviceRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DeviceResponseDto mapToDto(Device device) {
        DeviceResponseDto dto = new DeviceResponseDto();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setServiceUuid(device.getServiceUuid());
        dto.setReadNotifyCharacteristicUuid(device.getReadNotifyCharacteristicUuid());
        dto.setWriteCharacteristicUuid(device.getWriteCharacteristicUuid());
        return dto;
    }

    public DeviceResponseDto getDeviceById(Long id) {
        AppUser currentUser = getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this device");
        }

        return mapToDto(device);
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public DeviceResponseDto updateDevice(Long id, DeviceUpdateRequestDto dto) {
        AppUser currentUser = getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this device");
        }

        device.setName(dto.getName());
        device.setServiceUuid(dto.getServiceUuid());
        device.setReadNotifyCharacteristicUuid(dto.getReadNotifyCharacteristicUuid());
        device.setWriteCharacteristicUuid(dto.getWriteCharacteristicUuid());

        Device updated = deviceRepository.save(device);
        return mapToDto(updated);
    }
}


