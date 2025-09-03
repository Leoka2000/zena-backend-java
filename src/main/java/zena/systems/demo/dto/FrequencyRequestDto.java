package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrequencyRequestDto {
    private Integer freq1;
    private Integer freq2;
    private Integer freq3;
    private Integer freq4;
    private Long timestamp;
    private Long deviceId;
}