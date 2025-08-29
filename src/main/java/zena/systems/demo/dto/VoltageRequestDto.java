package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoltageRequestDto {
    private Float voltage;
    private Long timestamp;
    private Long deviceId;
}