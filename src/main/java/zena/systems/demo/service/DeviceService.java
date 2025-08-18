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
        return createDeviceWithUser(dto, currentUser);
    }

    @Transactional
    public DeviceResponseDto createDeviceFromBluetooth(String name, String serviceUuid,
            String notifyCharUuid, String writeCharUuid) {
        AppUser currentUser = getCurrentUser();
        DeviceRequestDto dto = new DeviceRequestDto();
        dto.setName(name);
        dto.setServiceUuid(serviceUuid);
        dto.setReadNotifyCharacteristicUuid(notifyCharUuid);
        dto.setWriteCharacteristicUuid(writeCharUuid);

        return createDeviceWithUser(dto, currentUser);
    }

    private DeviceResponseDto createDeviceWithUser(DeviceRequestDto dto, AppUser user) {
        Device device = new Device();
        device.setServiceUuid(dto.getServiceUuid());
        device.setReadNotifyCharacteristicUuid(dto.getReadNotifyCharacteristicUuid());
        device.setWriteCharacteristicUuid(dto.getWriteCharacteristicUuid());
        device.setName(dto.getName());
        device.setUser(user);

        Device saved = deviceRepository.save(device);

        // Create default sensor data
        createDefaultSensorData(saved, user);

        // Update user flag if needed
        if (!user.isHasCreatedFirstDevice()) {
            user.setHasCreatedFirstDevice(true);
            userRepository.save(user);
        }

        return mapToDto(saved);
    }

    private void createDefaultSensorData(Device device, AppUser user) {
        Temperature temp = new Temperature();
        temp.setTemperature(0.0f);
        temp.setTimestamp(System.currentTimeMillis() / 1000);
        temp.setDevice(device);
    
        temperatureRepository.save(temp);

        Accelerometer accel = new Accelerometer();
        accel.setX(0.0f);
        accel.setY(0.0f);
        accel.setZ(0.0f);
        accel.setTimestamp(System.currentTimeMillis() / 1000);
        accel.setDevice(device);
    
        accelerometerRepository.save(accel);

        Voltage voltage = new Voltage();
        voltage.setVoltage(0.0f);
        voltage.setTimestamp(System.currentTimeMillis() / 1000);
        voltage.setDevice(device);
      
        voltageRepository.save(voltage);
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

    @Transactional
    public void deleteDevice(Long id) {
        AppUser currentUser = getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        // Ensure the device belongs to the current user
        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this device");
        }

        // Delete related sensor data first
        temperatureRepository.deleteAll(temperatureRepository.findByDevice_IdOrderByCreatedAtAsc(id));
        accelerometerRepository.deleteAll(accelerometerRepository.findByDevice_IdOrderByCreatedAtAsc(id));
        voltageRepository.deleteAll(voltageRepository.findByDevice_IdOrderByCreatedAtAsc(id));

        // Delete the device itself
        deviceRepository.delete(device);
    }

}
