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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

        // Find device
        Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        // Ensure the device belongs to this user
        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        // Create and save the voltage record
        Voltage voltage = new Voltage();
        voltage.setVoltage(requestDTO.getVoltage());

        // Convert milliseconds to seconds if needed
        Long originalTimestamp = requestDTO.getTimestamp();
        Long correctedTimestamp = (originalTimestamp != null && originalTimestamp > 1_000_000_000_000L)
                ? originalTimestamp / 1000
                : originalTimestamp;

        voltage.setTimestamp(correctedTimestamp);
        voltage.setDevice(device);
        voltage.setUser(currentUser);

        voltageRepository.save(voltage);

        logger.info("Received Voltage Data for device {}: {}, corrected timestamp: {}",
                requestDTO.getDeviceId(), requestDTO, correctedTimestamp);
    }

    public List<VoltageResponseDto> getVoltageHistory(String range) {
        Long fromTimestamp = calculateFromTimestamp(range);
        logger.info("Fetching voltage history from timestamp: {}", fromTimestamp);

        List<Voltage> voltages = voltageRepository
                .findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);

        logger.info("Found {} voltage records", voltages.size());

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
        Instant now = Instant.now();
        return switch (range) {
            case "week" -> now.minusSeconds(7 * 24 * 60 * 60).getEpochSecond();
            case "month" -> now.minusSeconds(30L * 24 * 60 * 60).getEpochSecond();
            case "3months" -> now.minusSeconds(90L * 24 * 60 * 60).getEpochSecond();
            default -> now.minusSeconds(24 * 60 * 60).getEpochSecond();
        };
    }

    private VoltageResponseDto convertToResponseDTO(Voltage voltage) {
        VoltageResponseDto dto = new VoltageResponseDto();
        dto.setVoltage(voltage.getVoltage());
        dto.setTimestamp(voltage.getTimestamp());

        Instant instant = Instant.ofEpochSecond(voltage.getTimestamp());
        String isoDate = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.systemDefault())
                .format(instant);
        dto.setDate(isoDate);

        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
