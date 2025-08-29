package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
