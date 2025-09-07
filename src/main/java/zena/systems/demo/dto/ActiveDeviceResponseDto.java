package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveDeviceResponseDto {
    private Long deviceId;
    private String deviceName;
    private Long userId;
    private String serviceUuid;
    private String measurementCharUuid;
    private String logReadCharUuid;
    private String setTimeCharUuid;
    private String ledControlCharUuid;
    private String sleepControlCharUuid;
    private Long lastReceivedTimestamp;
    private String alarmCharUuid;
    private boolean isRegisteredDevice;
}
