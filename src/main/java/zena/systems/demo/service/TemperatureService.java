package zena.systems.demo.service;

import zena.systems.demo.dto.TemperatureRequestDto;
import zena.systems.demo.dto.TemperatureResponseDto;
import zena.systems.demo.model.Temperature;
import zena.systems.demo.repository.TemperatureRepository;
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
public class TemperatureService {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureService.class);
    private final TemperatureRepository temperatureRepository;

    public void saveTemperature(TemperatureRequestDto requestDTO) {
        Temperature temperature = new Temperature();
        temperature.setTemperature(requestDTO.getTemperature());
        temperature.setTimestamp(requestDTO.getTimestamp());
        
        temperatureRepository.save(temperature);
        logger.info("Received Temperature Data: {}", requestDTO);
    }

    public List<TemperatureResponseDto> getTemperatureHistory(String range) {
        Long fromTimestamp = calculateFromTimestamp(range);
        List<Temperature> temperatures = temperatureRepository
            .findByTimestampGreaterThanEqualOrderByTimestampAsc(fromTimestamp);

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

    private Long calculateFromTimestamp(String range) {
        Instant now = Instant.now();
        return switch (range) {
            case "week" -> now.minusSeconds(7 * 24 * 60 * 60).getEpochSecond();
            case "month" -> now.minusSeconds(30L * 24 * 60 * 60).getEpochSecond();
            case "3months" -> now.minusSeconds(90L * 24 * 60 * 60).getEpochSecond();
            default -> now.minusSeconds(24 * 60 * 60).getEpochSecond(); // default to day
        };
    }

    private TemperatureResponseDto convertToResponseDTO(Temperature temperature) {
        TemperatureResponseDto dto = new TemperatureResponseDto();
        dto.setTemperature(temperature.getTemperature());
        dto.setTimestamp(temperature.getTimestamp());
        
        // Convert timestamp to ISO string
        Instant instant = Instant.ofEpochSecond(temperature.getTimestamp());
        String isoDate = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.systemDefault())
                .format(instant);
        dto.setDate(isoDate);
        
        return dto;
    }
}
