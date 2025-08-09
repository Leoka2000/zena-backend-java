package zena.systems.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zena.systems.demo.dto.DeviceRequestDto;
import zena.systems.demo.dto.DeviceResponseDto;
import zena.systems.demo.dto.DeviceUpdateRequestDto;
import zena.systems.demo.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/create")
    public ResponseEntity<DeviceResponseDto> createDevice(@RequestBody DeviceRequestDto dto) {
        DeviceResponseDto created = deviceService.createDevice(dto);
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

}