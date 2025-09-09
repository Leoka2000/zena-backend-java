package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.ActiveDeviceResponseDto;
import zena.systems.demo.model.ActiveDevice;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.repository.ActiveDeviceRepository;
import zena.systems.demo.repository.DeviceRepository;

@Service
@RequiredArgsConstructor
public class ActiveDeviceService {
    private final ActiveDeviceRepository activeDeviceRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public ActiveDeviceResponseDto setActiveDevice(AppUser user, Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this device");
        }

        ActiveDevice activeDevice = activeDeviceRepository.findByUser(user);

        if (activeDevice != null) {
            activeDevice.setDevice(device);
        } else {
            activeDevice = new ActiveDevice();
            activeDevice.setUser(user);
            activeDevice.setDevice(device);
        }

        activeDeviceRepository.save(activeDevice);
        return mapToActiveDeviceDto(device);
    }

    public ActiveDeviceResponseDto getActiveDevice(AppUser user) {
        ActiveDevice activeDevice = activeDeviceRepository.findByUser(user);
        if (activeDevice == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active device set");
        }
        return mapToActiveDeviceDto(activeDevice.getDevice());
    }

    public ActiveDevice getActiveDeviceEntity(AppUser user) {
        return activeDeviceRepository.findByUser(user);
    }

    private ActiveDeviceResponseDto mapToActiveDeviceDto(Device device) {
        ActiveDeviceResponseDto dto = new ActiveDeviceResponseDto();
        dto.setDeviceId(device.getId());
        dto.setDeviceName(device.getName());
        dto.setUserId(device.getUser().getId());
        dto.setServiceUuid(device.getServiceUuid());
        dto.setMeasurementCharUuid(device.getMeasurementCharUuid());
        dto.setLogReadCharUuid(device.getLogReadCharUuid());
        dto.setSetTimeCharUuid(device.getSetTimeCharUuid());
        dto.setLedControlCharUuid(device.getLedControlCharUuid());
        dto.setSleepControlCharUuid(device.getSleepControlCharUuid());
        dto.setAlarmCharUuid(device.getAlarmCharUuid());
        dto.setLastReceivedTimestamp(device.getLastReceivedTimestamp());
        dto.setRegisteredDevice(device.isRegisteredDevice());
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

}
