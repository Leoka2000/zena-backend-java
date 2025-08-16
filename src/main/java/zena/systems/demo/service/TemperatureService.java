package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.TemperatureRequestDto;
import zena.systems.demo.dto.TemperatureResponseDto;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.model.Temperature;
import zena.systems.demo.repository.DeviceRepository;
import zena.systems.demo.repository.TemperatureRepository;
import zena.systems.demo.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemperatureService {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureService.class);

    private final TemperatureRepository temperatureRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void saveTemperature(TemperatureRequestDto requestDTO) {
        AppUser currentUser = getCurrentUser();

        Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        Temperature temperature = new Temperature();
        temperature.setTemperature(requestDTO.getTemperature());
        // We no longer manually set timestamp; createdAt will be populated
        // automatically
        temperature.setDevice(device);
        temperature.setUser(currentUser);

        temperatureRepository.save(temperature);

        logger.info("Received Temperature Data for device {}: {}", requestDTO.getDeviceId(), requestDTO);
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<TemperatureResponseDto> getTemperatureHistory(String range, Long deviceId) {
        Instant fromCreatedAt = calculateFromInstant(range);

        List<Temperature> temperatures;

        if (deviceId != null) {
            if (range != null && !range.isEmpty()) {
                temperatures = temperatureRepository
                        .findByDevice_IdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(deviceId, fromCreatedAt);
            } else {
                temperatures = temperatureRepository.findByDevice_IdOrderByCreatedAtAsc(deviceId);
            }
        } else {
            if (range != null && !range.isEmpty()) {
                temperatures = temperatureRepository.findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(fromCreatedAt);
            } else {
                temperatures = temperatureRepository.findAllByOrderByCreatedAtAsc();
            }
        }

        return temperatures.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TemperatureResponseDto> getAllTemperatureHistory() {
        List<Temperature> temperatures = temperatureRepository.findAllByOrderByCreatedAtAsc();
        return temperatures.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private Instant calculateFromInstant(String range) {
        Instant now = Instant.now();
        return switch (range) {
            case "week" -> now.minusSeconds(7 * 24 * 60 * 60);
            case "month" -> now.minusSeconds(30L * 24 * 60 * 60);
            case "3months" -> now.minusSeconds(90L * 24 * 60 * 60);
            default -> now.minusSeconds(24 * 60 * 60);
        };
    }

    private TemperatureResponseDto convertToResponseDTO(Temperature temperature) {
        TemperatureResponseDto dto = new TemperatureResponseDto();
        dto.setTemperature(temperature.getTemperature());

        // Use createdAt instead of timestamp
        long epochSeconds = temperature.getCreatedAt().getEpochSecond();
        dto.setTimestamp(epochSeconds);

        String isoDate = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.systemDefault())
                .format(temperature.getCreatedAt());
        dto.setDate(isoDate);

        return dto;
    }
}