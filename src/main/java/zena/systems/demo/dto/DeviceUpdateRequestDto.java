package zena.systems.demo.dto;

import lombok.Data;

@Data
public class DeviceUpdateRequestDto {
    private Long id; // optional if you want to cross-check path variable id with this
    private String name;
    private String serviceUuid;
    private String readNotifyCharacteristicUuid;
    private String writeCharacteristicUuid;
}
