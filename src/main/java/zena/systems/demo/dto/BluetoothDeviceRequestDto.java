package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BluetoothDeviceRequestDto {
    private String name;
    private String serviceUuid;
    private String notifyCharacteristicUuid;
    private String writeCharacteristicUuid;
}