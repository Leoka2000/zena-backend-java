package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.AccelerometerRequestDto;
import zena.systems.demo.dto.AccelerometerResponseDto;
import zena.systems.demo.model.Accelerometer;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.repository.AccelerometerRepository;
import zena.systems.demo.repository.DeviceRepository;
import zena.systems.demo.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccelerometerService {
    private static final Logger logger = LoggerFactory.getLogger(AccelerometerService.class);

    private final AccelerometerRepository accelerometerRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void saveAccelerometerData(AccelerometerRequestDto requestDto) {
        AppUser currentUser = getCurrentUser();

        // Find and validate device ownership
        Device device = deviceRepository.findById(requestDto.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        // Create new Accelerometer record
        Accelerometer accelerometer = new Accelerometer();
        accelerometer.setX(requestDto.getX());
        accelerometer.setY(requestDto.getY());
        accelerometer.setZ(requestDto.getZ());

        // Correct timestamp if in milliseconds
        Long originalTimestamp = requestDto.getTimestamp();
        Long correctedTimestamp = (originalTimestamp != null && originalTimestamp > 1_000_000_000_000L)
                ? originalTimestamp / 1000
                : originalTimestamp;
        accelerometer.setTimestamp(correctedTimestamp);

        accelerometer.setDevice(device);
        accelerometer.setUser(currentUser);

        accelerometerRepository.save(accelerometer);
        logger.info("Received Accelerometer Data for device {}: {}, corrected timestamp: {}",
                requestDto.getDeviceId(), requestDto, correctedTimestamp);
    }

    public List<AccelerometerResponseDto> getAccelerometerHistory(String range) {
        Long fromTimestamp = calculateFromTimestamp(range);
        List<Accelerometer> accelerometers = accelerometerRepository
                .findByTimestampGreaterThanEqualOrderByTimestamp(fromTimestamp);

        return accelerometers.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AccelerometerResponseDto> getAllAccelerometerHistory() {
        List<Accelerometer> accelerometers = accelerometerRepository.findAllByOrderByTimestampAsc();
        return accelerometers.stream()
                .map(this::convertToResponseDto)
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

    private AccelerometerResponseDto convertToResponseDto(Accelerometer accelerometer) {
        AccelerometerResponseDto dto = new AccelerometerResponseDto();
        dto.setX(accelerometer.getX());
        dto.setY(accelerometer.getY());
        dto.setZ(accelerometer.getZ());
        dto.setTimestamp(accelerometer.getTimestamp());

        Instant instant = Instant.ofEpochSecond(accelerometer.getTimestamp());
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
