package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoltageResponseDto {
    private Float voltage;
    private Long timestamp;
    private String date; // ISO formatted
}
