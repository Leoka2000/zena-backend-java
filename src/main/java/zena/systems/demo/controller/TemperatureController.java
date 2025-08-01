package zena.systems.demo.controller;

import zena.systems.demo.dto.TemperatureRequestDto;
import zena.systems.demo.dto.TemperatureResponseDto;
import zena.systems.demo.service.TemperatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/temperature")
@RequiredArgsConstructor
public class TemperatureController {
    private final TemperatureService temperatureService;

    @PostMapping
    public ResponseEntity<String> storeTemperature(@RequestBody TemperatureRequestDto requestDTO) {
        temperatureService.saveTemperature(requestDTO);
        return ResponseEntity.ok("Data received successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<TemperatureResponseDto>> getTemperatureHistory() {
        List<TemperatureResponseDto> history = temperatureService.getTemperatureHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/debug")
    public String debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Current auth: " + auth + "\nPrincipal: " + auth.getPrincipal();
    }
}