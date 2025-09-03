package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.FrequencyRequestDto;
import zena.systems.demo.dto.FrequencyResponseDto;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.model.Frequency;
import zena.systems.demo.repository.DeviceRepository;
import zena.systems.demo.repository.FrequencyRepository;
import zena.systems.demo.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FrequencyService {
    private static final Logger logger = LoggerFactory.getLogger(FrequencyService.class);

    private final FrequencyRepository frequencyRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void saveFrequency(FrequencyRequestDto requestDTO) {
        AppUser currentUser = getCurrentUser();

        Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        Frequency frequency = new Frequency();
        frequency.setFreq1(requestDTO.getFreq1());
        frequency.setFreq2(requestDTO.getFreq2());
        frequency.setFreq3(requestDTO.getFreq3());
        frequency.setFreq4(requestDTO.getFreq4());
        frequency.setTimestamp(requestDTO.getTimestamp());
        frequency.setDevice(device);

        frequencyRepository.save(frequency);

        logger.info("Received Frequency Data for device {}: {}", requestDTO.getDeviceId(), requestDTO);
    }

    public List<FrequencyResponseDto> getFrequencyHistory(String range, Long deviceId) {
        Long fromTimestamp = calculateFromTimestamp(range);

        List<Frequency> frequencies;

        if (deviceId != null) {
            if (range != null && !range.isEmpty()) {
                frequencies = frequencyRepository
                        .findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(deviceId, fromTimestamp);
            } else {
                frequencies = frequencyRepository.findByDevice_IdOrderByTimestampAsc(deviceId);
            }
        } else {
            if (range != null && !range.isEmpty()) {
                frequencies = frequencyRepository.findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);
            } else {
                frequencies = frequencyRepository.findAllByOrderByTimestampAsc();
            }
        }

        return frequencies.stream()
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

    private FrequencyResponseDto convertToResponseDTO(Frequency frequency) {
        FrequencyResponseDto dto = new FrequencyResponseDto();
        dto.setFreq1(frequency.getFreq1());
        dto.setFreq2(frequency.getFreq2());
        dto.setFreq3(frequency.getFreq3());
        dto.setFreq4(frequency.getFreq4());
        dto.setTimestamp(frequency.getTimestamp().toString());
        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}