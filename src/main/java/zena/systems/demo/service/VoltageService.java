package zena.systems.demo.service;

import zena.systems.demo.dto.VoltageRequestDto;
import zena.systems.demo.dto.VoltageResponseDto;
import zena.systems.demo.model.Voltage;
import zena.systems.demo.repository.VoltageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public void saveVoltage(VoltageRequestDto requestDTO) {
        Voltage voltage = new Voltage();
        voltage.setVoltage(requestDTO.getVoltage());

        // Convert milliseconds to seconds if needed (e.g., timestamp > 10^12 = ms)
        Long originalTimestamp = requestDTO.getTimestamp();
        Long correctedTimestamp = (originalTimestamp != null && originalTimestamp > 1_000_000_000_000L)
                ? originalTimestamp / 1000
                : originalTimestamp;

        voltage.setTimestamp(correctedTimestamp);

        voltageRepository.save(voltage);
        logger.info("Received Voltage Data: {}, corrected timestamp: {}", requestDTO, correctedTimestamp);
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
            default -> now.minusSeconds(24 * 60 * 60).getEpochSecond(); // default to day
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
}
