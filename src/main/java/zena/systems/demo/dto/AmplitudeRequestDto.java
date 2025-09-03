package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmplitudeRequestDto {
    private Integer ampl1;
    private Integer ampl2;
    private Integer ampl3;
    private Integer ampl4;
    private Long timestamp;
    private Long deviceId;
}