package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

    public DeviceResponseDto createDevice(DeviceRequestDto dto) {
        AppUser currentUser = getCurrentUser();
        Device device = new Device();
        device.setServiceUuid(dto.getServiceUuid());
        device.setReadNotifyCharacteristicUuid(dto.getReadNotifyCharacteristicUuid());
        device.setWriteCharacteristicUuid(dto.getWriteCharacteristicUuid());
        device.setName(dto.getName());
        device.setUser(currentUser);

        Device saved = deviceRepository.save(device);

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

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
