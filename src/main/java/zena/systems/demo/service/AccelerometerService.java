package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
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
    private final AccelerometerRepository accelerometerRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void saveAccelerometerData(AccelerometerRequestDto requestDto) {
        AppUser currentUser = getCurrentUser();

        // Find device
        Device device = deviceRepository.findById(requestDto.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        // Ensure ownership
        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        // Create new record
        Accelerometer accelerometer = new Accelerometer();
        accelerometer.setX(requestDto.getX());
        accelerometer.setY(requestDto.getY());
        accelerometer.setZ(requestDto.getZ());
        accelerometer.setDevice(device);
        accelerometer.setUser(currentUser);

        accelerometerRepository.save(accelerometer);
    }

    public List<AccelerometerResponseDto> getAccelerometerHistory(String range) {
        Instant fromTimestamp = calculateFromTimestamp(range);
        List<Accelerometer> accelerometers = accelerometerRepository
                .findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(fromTimestamp);

        return accelerometers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AccelerometerResponseDto> getAllAccelerometerHistory() {
        List<Accelerometer> accelerometers = accelerometerRepository.findAllByOrderByCreatedAtAsc();
        return accelerometers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private Instant calculateFromTimestamp(String range) {
        Instant now = Instant.now();
        return switch (range) {
            case "week" -> now.minusSeconds(7 * 24 * 60 * 60);
            case "month" -> now.minusSeconds(30L * 24 * 60 * 60);
            case "3months" -> now.minusSeconds(90L * 24 * 60 * 60);
            default -> now.minusSeconds(24 * 60 * 60);
        };
    }

    private AccelerometerResponseDto convertToResponseDTO(Accelerometer accelerometer) {
        AccelerometerResponseDto dto = new AccelerometerResponseDto();
        dto.setX(accelerometer.getX());
        dto.setY(accelerometer.getY());
        dto.setZ(accelerometer.getZ());

        Instant createdAt = accelerometer.getCreatedAt();
        dto.setTimestamp(createdAt.getEpochSecond());

        String isoDate = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.systemDefault())
                .format(createdAt);
        dto.setDate(isoDate);

        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
