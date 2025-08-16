package zena.systems.demo.controller;

import zena.systems.demo.dto.AccelerometerRequestDto;
import zena.systems.demo.dto.AccelerometerResponseDto;
import zena.systems.demo.service.AccelerometerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accelerometer")
@RequiredArgsConstructor
public class AccelerometerController {
    private final AccelerometerService accelerometerService;

    @PostMapping
    public ResponseEntity<String> storeAccelerometerData(
            @RequestBody AccelerometerRequestDto requestDto) {
        accelerometerService.saveAccelerometerData(requestDto);
        return ResponseEntity.ok("Accelerometer data stored successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<AccelerometerResponseDto>> getAccelerometerHistory(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) Long deviceId) {
        
        return ResponseEntity.ok(accelerometerService.getAccelerometerHistory(range, deviceId));
    }
}