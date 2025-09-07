package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class DeviceResponseDto {
    private Long id;
    private String name;
    private String serviceUuid;
    private String measurementCharUuid;
    private String logReadCharUuid;
    private String setTimeCharUuid;
    private String ledControlCharUuid;
    private String sleepControlCharUuid;
    private String alarmCharUuid;
    private boolean isRegisteredDevice;
    private Instant createdAt;
    private Long lastReceivedTimestamp;

}
