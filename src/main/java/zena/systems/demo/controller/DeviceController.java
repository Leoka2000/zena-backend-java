package zena.systems.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import zena.systems.demo.dto.*;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.service.ActiveDeviceService;
import zena.systems.demo.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final ActiveDeviceService activeDeviceService;

    @PostMapping("/create")
    public ResponseEntity<DeviceResponseDto> createDevice(@RequestBody DeviceRequestDto dto) {
        DeviceResponseDto created = deviceService.createDevice(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/create-from-bluetooth")
    public ResponseEntity<DeviceResponseDto> createDeviceFromBluetooth(
            @RequestBody BluetoothDeviceRequestDto dto,
            @AuthenticationPrincipal AppUser user) {
        DeviceResponseDto created = deviceService.createDeviceFromBluetooth(
                dto.getName(),
                dto.getServiceUuid(),
                dto.getNotifyCharacteristicUuid(),
                dto.getWriteCharacteristicUuid());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/list")
    public ResponseEntity<List<DeviceResponseDto>> listUserDevices() {
        List<DeviceResponseDto> devices = deviceService.getUserDevices();
        return ResponseEntity.ok(devices);
    }

    @PatchMapping("/list/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(
            @PathVariable Long id,
            @RequestBody DeviceUpdateRequestDto updateDto) {
        DeviceResponseDto updatedDevice = deviceService.updateDevice(id, updateDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @PostMapping("/select")
    public ResponseEntity<ActiveDeviceResponseDto> selectActiveDevice(
            @RequestParam Long deviceId,
            @AuthenticationPrincipal AppUser user) {
        ActiveDeviceResponseDto dto = activeDeviceService.setActiveDevice(user, deviceId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/active")
    public ResponseEntity<ActiveDeviceResponseDto> getActiveDevice(
            @AuthenticationPrincipal AppUser user) {
        ActiveDeviceResponseDto dto = activeDeviceService.getActiveDevice(user);
        return ResponseEntity.ok(dto);
    }
}