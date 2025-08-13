package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveDeviceResponseDto {
    private Long deviceId;
    private String deviceName;
    private String serviceUuid;
}