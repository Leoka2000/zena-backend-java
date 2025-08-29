package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.DeviceRequestDto;
import zena.systems.demo.dto.DeviceResponseDto;
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

    @Transactional
    public DeviceResponseDto createDevice(DeviceRequestDto dto) {
        AppUser currentUser = getCurrentUser();
        Device device = mapDtoToDevice(dto, currentUser);
        Device saved = deviceRepository.save(device);
        return mapToDto(saved);
    }

    private Device mapDtoToDevice(DeviceRequestDto dto, AppUser user) {
        Device device = new Device();
        device.setName(dto.getName());
        device.setServiceUuid(dto.getServiceUuid());
        device.setMeasurementCharUuid(dto.getMeasurementCharUuid());
        device.setLogReadCharUuid(dto.getLogReadCharUuid());
        device.setSetTimeCharUuid(dto.getSetTimeCharUuid());
        device.setLedControlCharUuid(dto.getLedControlCharUuid());
        device.setSleepControlCharUuid(dto.getSleepControlCharUuid());
        device.setAlarmCharUuid(dto.getAlarmCharUuid());
        device.setUser(user);
        return device;
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

        device.setName(dto.getName());
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
        dto.setCreatedAt(device.getCreatedAt());
        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
