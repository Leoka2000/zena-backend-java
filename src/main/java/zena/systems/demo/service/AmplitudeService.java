package zena.systems.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zena.systems.demo.dto.AmplitudeRequestDto;
import zena.systems.demo.dto.AmplitudeResponseDto;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Device;
import zena.systems.demo.model.Amplitude;
import zena.systems.demo.repository.DeviceRepository;
import zena.systems.demo.repository.AmplitudeRepository;
import zena.systems.demo.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmplitudeService {
    private static final Logger logger = LoggerFactory.getLogger(AmplitudeService.class);

    private final AmplitudeRepository amplitudeRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void saveAmplitude(AmplitudeRequestDto requestDTO) {
        AppUser currentUser = getCurrentUser();

        Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this device");
        }

        Amplitude amplitude = new Amplitude();
        amplitude.setAmpl1(requestDTO.getAmpl1());
        amplitude.setAmpl2(requestDTO.getAmpl2());
        amplitude.setAmpl3(requestDTO.getAmpl3());
        amplitude.setAmpl4(requestDTO.getAmpl4());
        amplitude.setTimestamp(requestDTO.getTimestamp());
        amplitude.setDevice(device);

        amplitudeRepository.save(amplitude);

        logger.info("Received Amplitude Data for device {}: {}", requestDTO.getDeviceId(), requestDTO);
    }

    public List<AmplitudeResponseDto> getAmplitudeHistory(String range, Long deviceId) {
        Long fromTimestamp = calculateFromTimestamp(range);

        List<Amplitude> amplitudes;

        if (deviceId != null) {
            if (range != null && !range.isEmpty()) {
                amplitudes = amplitudeRepository
                        .findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(deviceId, fromTimestamp);
            } else {
                amplitudes = amplitudeRepository.findByDevice_IdOrderByTimestampAsc(deviceId);
            }
        } else {
            if (range != null && !range.isEmpty()) {
                amplitudes = amplitudeRepository.findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);
            } else {
                amplitudes = amplitudeRepository.findAllByOrderByTimestampAsc();
            }
        }

        return amplitudes.stream()
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

    private AmplitudeResponseDto convertToResponseDTO(Amplitude amplitude) {
        AmplitudeResponseDto dto = new AmplitudeResponseDto();
        dto.setAmpl1(amplitude.getAmpl1());
        dto.setAmpl2(amplitude.getAmpl2());
        dto.setAmpl3(amplitude.getAmpl3());
        dto.setAmpl4(amplitude.getAmpl4());
        dto.setTimestamp(amplitude.getTimestamp().toString());
        return dto;
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}