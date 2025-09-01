package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemperatureResponseDto {
    private Long timestamp;   // raw Unix epoch
    private Float temperature;

}
