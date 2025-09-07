package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.VoltageRequestDto;
import zena.systems.demo.dto.VoltageResponseDto;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.model.Voltage;
import zena.systems.demo.repository.DeviceRepository;
import zena.systems.demo.repository.UserRepository;
import zena.systems.demo.repository.VoltageRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoltageService {
    private static final Logger logger = LoggerFactory.getLogger(VoltageService.class);

    private final VoltageRepository voltageRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void saveVoltage(VoltageRequestDto requestDTO) {
        AppUser currentUser = getCurrentUser();

        Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        Voltage voltage = new Voltage();
        voltage.setVoltage(requestDTO.getVoltage());
        voltage.setTimestamp(requestDTO.getTimestamp()); // ✅ now storing BLE timestamp
        voltage.setDevice(device);

        voltageRepository.save(voltage);

        device.setLatestVoltage(requestDTO.getVoltage());
        device.setLastReceivedTimestamp(requestDTO.getTimestamp());
        deviceRepository.save(device);

        logger.info("Received Voltage Data for device {}: {}", requestDTO.getDeviceId(), requestDTO);
    }

    public List<VoltageResponseDto> getVoltageHistory(String range, Long deviceId) {
        Long fromTimestamp = calculateFromTimestamp(range);

        List<Voltage> voltages;

        if (deviceId != null) {
            if (range != null && !range.isEmpty()) {
                voltages = voltageRepository
                        .findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(deviceId, fromTimestamp);
            } else {
                voltages = voltageRepository.findByDevice_IdOrderByTimestampAsc(deviceId);
            }
        } else {
            if (range != null && !range.isEmpty()) {
                voltages = voltageRepository.findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);
            } else {
                voltages = voltageRepository.findAllByOrderByTimestampAsc();
            }
        }

        return voltages.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VoltageResponseDto> getAllVoltageHistory() {
        List<Voltage> voltages = voltageRepository.findAllByOrderByTimestampAsc();
        return voltages.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private Long calculateFromTimestamp(String range) {
        long now = Instant.now().getEpochSecond();
        return switch (range) {
            case "week" -> now - (7L * 24 * 60 * 60);
            case "month" -> now - (30L * 24 * 60 * 60);
            case "3months" -> now - (90L * 24 * 60 * 60);
            default -> now - (24 * 60 * 60);
        };
    }

    private VoltageResponseDto convertToResponseDTO(Voltage voltage) {
        VoltageResponseDto dto = new VoltageResponseDto();
        dto.setVoltage(voltage.getVoltage());
        dto.setTimestamp(voltage.getTimestamp().toString()); // ✅ returning timestamp as String
        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
