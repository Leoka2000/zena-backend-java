package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceLatestDataDto {
    private Long deviceId;
    private Float temperature;
    private Float voltage;
    private Float accelX;
    private Float accelY;
    private Float accelZ;
    private Integer freq1;
    private Integer freq2;
    private Integer freq3;
    private Integer freq4;
    private Integer ampl1;
    private Integer ampl2;
    private Integer ampl3;
    private Integer ampl4;
    private Long lastReceivedTimestamp;
}
