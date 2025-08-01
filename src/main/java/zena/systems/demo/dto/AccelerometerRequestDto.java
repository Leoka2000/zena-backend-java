package zena.systems.demo.dto;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccelerometerRequestDto {
    private Float x;
    private Float y;
    private Float z;
    private Long timestamp;
}