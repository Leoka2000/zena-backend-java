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
        temperature.setTimestamp(requestDTO.getTimestamp()); // <-- Use MCU timestamp
        temperature.setDevice(device);

        temperatureRepository.save(temperature);

        device.setLatestTemperature(requestDTO.getTemperature());
        device.setLastReceivedTimestamp(requestDTO.getTimestamp());
        deviceRepository.save(device);

        logger.info("Received Temperature Data for device {}: {}", requestDTO.getDeviceId(), requestDTO);
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<TemperatureResponseDto> getTemperatureHistory(String range, Long deviceId) {
        long fromTimestamp = calculateFromTimestamp(range);

        List<Temperature> temperatures;

        if (deviceId != null) {
            if (range != null && !range.isEmpty()) {
                temperatures = temperatureRepository
                        .findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(deviceId, fromTimestamp);
            } else {
                temperatures = temperatureRepository.findByDevice_IdOrderByTimestampAsc(deviceId);
            }
        } else {
            if (range != null && !range.isEmpty()) {
                temperatures = temperatureRepository.findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);
            } else {
                temperatures = temperatureRepository.findAllByOrderByTimestampAsc();
            }
        }

        return temperatures.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TemperatureResponseDto> getAllTemperatureHistory() {
        List<Temperature> temperatures = temperatureRepository.findAllByOrderByTimestampAsc();
        return temperatures.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private long calculateFromTimestamp(String range) {
        long now = Instant.now().getEpochSecond();
        return switch (range) {
            case "day" -> now - 24 * 60 * 60;
            case "week" -> now - 7 * 24 * 60 * 60;
            case "month" -> now - 30L * 24 * 60 * 60;
            case "3months" -> now - 90L * 24 * 60 * 60;
            default -> now - 24 * 60 * 60;
        };
    }

    private TemperatureResponseDto convertToResponseDTO(Temperature temperature) {
        TemperatureResponseDto dto = new TemperatureResponseDto();
        dto.setTemperature(temperature.getTemperature());

        long epochSeconds = temperature.getTimestamp();
        dto.setTimestamp(epochSeconds);

        return dto;
    }
}
