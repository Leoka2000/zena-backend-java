package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequestDto {
    private String serviceUuid;
    private String readNotifyCharacteristicUuid;
    private String writeCharacteristicUuid;
    private String name;
}
