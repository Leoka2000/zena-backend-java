package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import zena.systems.demo.dto.DeviceLatestDataDto;
import zena.systems.demo.dto.DeviceRequestDto;
import zena.systems.demo.dto.DeviceResponseDto;
import zena.systems.demo.dto.RegisterDeviceDto;
import zena.systems.demo.model.ActiveDevice;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.repository.DeviceRepository;
import zena.systems.demo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final ActiveDeviceService activeDeviceService;

    @Transactional
    public DeviceResponseDto createDevice(DeviceRequestDto dto) {
        AppUser currentUser = getCurrentUser();
        Device device = new Device();
        device.setName(dto.getName());
        device.setRegisteredDevice(false); // initial state
        device.setUser(currentUser);
        Device saved = deviceRepository.save(device);
        return mapToDto(saved);
    }

    public List<DeviceResponseDto> getUserDevices() {
        AppUser user = getCurrentUser();
        return deviceRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DeviceResponseDto getDeviceById(Long id) {
        AppUser user = getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return mapToDto(device);
    }

    @Transactional
    public DeviceResponseDto updateDevice(Long id, DeviceRequestDto dto) {
        AppUser user = getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        device.setServiceUuid(dto.getServiceUuid());
        device.setMeasurementCharUuid(dto.getMeasurementCharUuid());
        device.setLogReadCharUuid(dto.getLogReadCharUuid());
        device.setSetTimeCharUuid(dto.getSetTimeCharUuid());
        device.setLedControlCharUuid(dto.getLedControlCharUuid());
        device.setSleepControlCharUuid(dto.getSleepControlCharUuid());
        device.setAlarmCharUuid(dto.getAlarmCharUuid());

        Device updated = deviceRepository.save(device);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteDevice(Long id) {
        AppUser user = getCurrentUser();
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        deviceRepository.delete(device);
    }

    @Transactional
    public DeviceResponseDto registerActiveDevice(AppUser user, RegisterDeviceDto dto) {
        ActiveDevice activeDevice = activeDeviceService.getActiveDeviceEntity(user);

        if (activeDevice == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active device to register");
        }

        Device device = activeDevice.getDevice();

        // Patch UUID fields
        device.setServiceUuid(dto.getServiceUuid());
        device.setMeasurementCharUuid(dto.getMeasurementCharUuid());
        device.setLogReadCharUuid(dto.getLogReadCharUuid());
        device.setSetTimeCharUuid(dto.getSetTimeCharUuid());
        device.setLedControlCharUuid(dto.getLedControlCharUuid());
        device.setSleepControlCharUuid(dto.getSleepControlCharUuid());
        device.setAlarmCharUuid(dto.getAlarmCharUuid());

        // **Mark device as registered**
        device.setRegisteredDevice(true);

        deviceRepository.save(device);
        return mapToDto(device); // mapToDto includes isRegisteredDevice
    }

    private DeviceResponseDto mapToDto(Device device) {
        DeviceResponseDto dto = new DeviceResponseDto();
        dto.setId(device.getId());
        dto.setName(device.getName());
        dto.setServiceUuid(device.getServiceUuid());
        dto.setMeasurementCharUuid(device.getMeasurementCharUuid());
        dto.setLogReadCharUuid(device.getLogReadCharUuid());
        dto.setSetTimeCharUuid(device.getSetTimeCharUuid());
        dto.setLedControlCharUuid(device.getLedControlCharUuid());
        dto.setSleepControlCharUuid(device.getSleepControlCharUuid());
        dto.setAlarmCharUuid(device.getAlarmCharUuid());
        dto.setRegisteredDevice(device.isRegisteredDevice());
        dto.setCreatedAt(device.getCreatedAt());
        dto.setLastReceivedTimestamp(device.getLastReceivedTimestamp());

        dto.setLatestTemperature(device.getLatestTemperature());
        dto.setLatestVoltage(device.getLatestVoltage());
        dto.setLatestAccelX(device.getLatestAccelX());
        dto.setLatestAccelY(device.getLatestAccelY());
        dto.setLatestAccelZ(device.getLatestAccelZ());
        dto.setLatestFreq1(device.getLatestFreq1());
        dto.setLatestFreq2(device.getLatestFreq2());
        dto.setLatestFreq3(device.getLatestFreq3());
        dto.setLatestFreq4(device.getLatestFreq4());
        dto.setLatestAmpl1(device.getLatestAmpl1());
        dto.setLatestAmpl2(device.getLatestAmpl2());
        dto.setLatestAmpl3(device.getLatestAmpl3());
        dto.setLatestAmpl4(device.getLatestAmpl4());

        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public DeviceResponseDto updateLastReceivedTimestamp(Long deviceId, Long timestamp) {
        AppUser user = getCurrentUser();
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        device.setLastReceivedTimestamp(timestamp);
        Device updated = deviceRepository.save(device);
        return mapToDto(updated);
    }

    @Transactional
    public DeviceResponseDto updateLatestData(DeviceLatestDataDto dto) {
        Device device = deviceRepository.findById(dto.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        // Update latest values
        if (dto.getTemperature() != null)
            device.setLatestTemperature(dto.getTemperature());
        if (dto.getVoltage() != null)
            device.setLatestVoltage(dto.getVoltage());
        if (dto.getAccelX() != null)
            device.setLatestAccelX(dto.getAccelX());
        if (dto.getAccelY() != null)
            device.setLatestAccelY(dto.getAccelY());
        if (dto.getAccelZ() != null)
            device.setLatestAccelZ(dto.getAccelZ());
        if (dto.getFreq1() != null)
            device.setLatestFreq1(dto.getFreq1());
        if (dto.getFreq2() != null)
            device.setLatestFreq2(dto.getFreq2());
        if (dto.getFreq3() != null)
            device.setLatestFreq3(dto.getFreq3());
        if (dto.getFreq4() != null)
            device.setLatestFreq4(dto.getFreq4());
        if (dto.getAmpl1() != null)
            device.setLatestAmpl1(dto.getAmpl1());
        if (dto.getAmpl2() != null)
            device.setLatestAmpl2(dto.getAmpl2());
        if (dto.getAmpl3() != null)
            device.setLatestAmpl3(dto.getAmpl3());
        if (dto.getAmpl4() != null)
            device.setLatestAmpl4(dto.getAmpl4());

     
        if (dto.getLastReceivedTimestamp() != null) {
            device.setLastReceivedTimestamp(dto.getLastReceivedTimestamp());
        } else {
            device.setLastReceivedTimestamp(System.currentTimeMillis());
        }

        Device updated = deviceRepository.save(device);
        return mapToDto(updated);
    }
}
