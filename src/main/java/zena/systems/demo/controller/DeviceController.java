package zena.systems.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import zena.systems.demo.dto.ActiveDeviceResponseDto;
import zena.systems.demo.dto.DeviceLatestDataDto;
import zena.systems.demo.dto.DeviceRequestDto;
import zena.systems.demo.dto.DeviceResponseDto;
import zena.systems.demo.dto.RegisterDeviceDto;
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

    @GetMapping("/list")
    public ResponseEntity<List<DeviceResponseDto>> listUserDevices() {
        return ResponseEntity.ok(deviceService.getUserDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(
            @PathVariable Long id,
            @RequestBody DeviceRequestDto dto) {
        return ResponseEntity.ok(deviceService.updateDevice(id, dto));
    }

    @DeleteMapping("/list/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build(); // 204 No Content
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

    @PatchMapping("/register")
    public ResponseEntity<DeviceResponseDto> registerActiveDevice(
            @AuthenticationPrincipal AppUser user,
            @RequestBody RegisterDeviceDto dto) {

        DeviceResponseDto updatedDevice = deviceService.registerActiveDevice(user, dto);
        return ResponseEntity.ok(updatedDevice);
    }


    @PatchMapping("/update-latest")
    public ResponseEntity<DeviceResponseDto> updateLatestData(
            @RequestBody DeviceLatestDataDto dto) {
        return ResponseEntity.ok(deviceService.updateLatestData(dto));
    }
}
