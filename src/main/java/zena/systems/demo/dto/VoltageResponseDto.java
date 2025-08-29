package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoltageResponseDto {
    private Float voltage;
    private String timestamp; // formatted as ISO string for charting
     // MAKE FUNCTION THAT CONVERTS THE "timestamp" 
     // which is unix format, before sending it as part of this DTO file.
}
