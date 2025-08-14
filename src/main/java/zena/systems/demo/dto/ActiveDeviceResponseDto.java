package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveDeviceResponseDto {
    private Long deviceId;
    private String deviceName;
    private String serviceUuid;
    private String readNotifyCharacteristicUuid;
    private String writeCharacteristicUuid;
    private Long userId; // Optional, if you want to show who owns the device
}