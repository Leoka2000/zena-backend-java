package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TemperatureResponseDto {
    private Long timestamp;
    private Float temperature;
    private String date; // ISO formatted date string
}