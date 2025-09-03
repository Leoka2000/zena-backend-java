package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrequencyResponseDto {
    private Integer freq1;
    private Integer freq2;
    private Integer freq3;
    private Integer freq4;
    private String timestamp; // formatted as string (UNIX timestamp as string)
}