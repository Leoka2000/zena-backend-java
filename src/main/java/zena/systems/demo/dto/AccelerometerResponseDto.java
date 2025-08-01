package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccelerometerResponseDto {
    private Long timestamp;
    private Float x;
    private Float y;
    private Float z;
    private String date; // ISO formatted date string
}