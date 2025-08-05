package zena.systems.demo.controller;

import zena.systems.demo.dto.VoltageRequestDto;
import zena.systems.demo.dto.VoltageResponseDto;
import zena.systems.demo.service.VoltageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voltage")
@RequiredArgsConstructor
public class VoltageController {
    private final VoltageService voltageService;

    @PostMapping
    public ResponseEntity<String> storeVoltage(@RequestBody VoltageRequestDto requestDTO) {
        voltageService.saveVoltage(requestDTO);
        return ResponseEntity.ok("Voltage data received successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<VoltageResponseDto>> getVoltageHistory(
            @RequestParam(required = false) String range) {

        if (range == null || range.isEmpty()) {
            return ResponseEntity.ok(voltageService.getAllVoltageHistory());
        }

        return ResponseEntity.ok(voltageService.getVoltageHistory(range));
    }

    @GetMapping("/debug")
    public String debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Current auth: " + auth + "\nPrincipal: " + auth.getPrincipal();
    }
}
