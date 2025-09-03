package zena.systems.demo.controller;

import zena.systems.demo.dto.FrequencyRequestDto;
import zena.systems.demo.dto.FrequencyResponseDto;
import zena.systems.demo.service.FrequencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frequency")
@RequiredArgsConstructor
public class FrequencyController {
    private final FrequencyService frequencyService;

    @PostMapping
    public ResponseEntity<String> storeFrequency(@RequestBody FrequencyRequestDto requestDTO) {
        frequencyService.saveFrequency(requestDTO);
        return ResponseEntity.ok("Frequency data received successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<FrequencyResponseDto>> getFrequencyHistory(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) Long deviceId) {
        return ResponseEntity.ok(frequencyService.getFrequencyHistory(range, deviceId));
    }
}