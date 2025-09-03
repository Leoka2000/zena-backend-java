package zena.systems.demo.controller;

import zena.systems.demo.dto.AmplitudeRequestDto;
import zena.systems.demo.dto.AmplitudeResponseDto;
import zena.systems.demo.service.AmplitudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amplitude")
@RequiredArgsConstructor
public class AmplitudeController {
    private final AmplitudeService amplitudeService;

    @PostMapping
    public ResponseEntity<String> storeAmplitude(@RequestBody AmplitudeRequestDto requestDTO) {
        amplitudeService.saveAmplitude(requestDTO);
        return ResponseEntity.ok("Amplitude data received successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<AmplitudeResponseDto>> getAmplitudeHistory(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) Long deviceId) {
        return ResponseEntity.ok(amplitudeService.getAmplitudeHistory(range, deviceId));
    }
}