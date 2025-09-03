package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmplitudeResponseDto {
    private Integer ampl1;
    private Integer ampl2;
    private Integer ampl3;
    private Integer ampl4;
    private String timestamp; // formatted as string (UNIX timestamp as string)
}