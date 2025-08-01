package zena.systems.demo.service;

import zena.systems.demo.dto.AccelerometerRequestDto;
import zena.systems.demo.dto.AccelerometerResponseDto;
import zena.systems.demo.model.Accelerometer;
import zena.systems.demo.repository.AccelerometerRepository;
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
public class AccelerometerService {
    private static final Logger logger = LoggerFactory.getLogger(AccelerometerService.class);
    private final AccelerometerRepository accelerometerRepository;

    public void saveAccelerometerData(AccelerometerRequestDto requestDto) {
        Accelerometer accelerometer = new Accelerometer();
        accelerometer.setX(requestDto.getX());
        accelerometer.setY(requestDto.getY());
        accelerometer.setZ(requestDto.getZ());
        accelerometer.setTimestamp(requestDto.getTimestamp());
        
        accelerometerRepository.save(accelerometer);
        logger.info("Received Accelerometer Data: {}", requestDto);
    }

    public List<AccelerometerResponseDto> getAccelerometerHistory(String range) {
        Long fromTimestamp = calculateFromTimestamp(range);
        List<Accelerometer> accelerometers = accelerometerRepository.findByTimestampGreaterThanEqualOrderByTimestamp(fromTimestamp);
        
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
            default -> now.minusSeconds(24 * 60 * 60).getEpochSecond(); // default to day
        };
    }

    private AccelerometerResponseDto convertToResponseDto(Accelerometer accelerometer) {
        AccelerometerResponseDto dto = new AccelerometerResponseDto();
        dto.setX(accelerometer.getX());
        dto.setY(accelerometer.getY());
        dto.setZ(accelerometer.getZ());
        dto.setTimestamp(accelerometer.getTimestamp());
        
        // Convert timestamp to ISO string
        Instant instant = Instant.ofEpochSecond(accelerometer.getTimestamp());
        String isoDate = DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.systemDefault())
                .format(instant);
        dto.setDate(isoDate);
        
        return dto;
    }
}