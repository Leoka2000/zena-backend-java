package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceResponseDto {
    private Long id;
    private String name;
    private String serviceUuid;
    private String readNotifyCharacteristicUuid;
    private String writeCharacteristicUuid;
}
