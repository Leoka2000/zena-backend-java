package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccelerometerResponseDto {
    private Float x;
    private Float y;
    private Float z;
    private String timestamp; // ISO formatted date string
}