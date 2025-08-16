package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class TemperatureRequestDto {
    private Float temperature;
    private Long deviceId;
}
