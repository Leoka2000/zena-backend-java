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

        Device device = deviceRepository.findById(requestDto.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        Accelerometer accelerometer = new Accelerometer();
        accelerometer.setX(requestDto.getX());
        accelerometer.setY(requestDto.getY());
        accelerometer.setZ(requestDto.getZ());
        accelerometer.setTimestamp(requestDto.getTimestamp());
        accelerometer.setDevice(device);

        accelerometerRepository.save(accelerometer);

        // ✅ Update latest values on Device
        device.setLatestAccelX(requestDto.getX());
        device.setLatestAccelY(requestDto.getY());
        device.setLatestAccelZ(requestDto.getZ());
        device.setLastReceivedTimestamp(requestDto.getTimestamp());
        deviceRepository.save(device);
    }

    public List<AccelerometerResponseDto> getAccelerometerHistory(String range, Long deviceId) {
        Long fromTimestamp = calculateFromTimestamp(range);

        List<Accelerometer> accelerometers;

        if (deviceId != null) {
            if (range != null && !range.isEmpty()) {
                accelerometers = accelerometerRepository
                        .findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(deviceId, fromTimestamp);
            } else {
                accelerometers = accelerometerRepository.findByDevice_IdOrderByTimestampAsc(deviceId);
            }
        } else {
            if (range != null && !range.isEmpty()) {
                accelerometers = accelerometerRepository
                        .findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);
            } else {
                accelerometers = accelerometerRepository.findAllByOrderByTimestampAsc();
            }
        }

        return accelerometers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private Long calculateFromTimestamp(String range) {
        long now = Instant.now().getEpochSecond();
        return switch (range) {
            case "week" -> now - 7 * 24 * 60 * 60;
            case "month" -> now - 30L * 24 * 60 * 60;
            case "3months" -> now - 90L * 24 * 60 * 60;
            default -> now - 24 * 60 * 60;
        };
    }

    private AccelerometerResponseDto convertToResponseDTO(Accelerometer accelerometer) {
        AccelerometerResponseDto dto = new AccelerometerResponseDto();
        dto.setX(accelerometer.getX());
        dto.setY(accelerometer.getY());
        dto.setZ(accelerometer.getZ());

        // Convert timestamp to ISO string
        String isoDate = Instant.ofEpochSecond(accelerometer.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_INSTANT);

        dto.setTimestamp(isoDate);
        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}