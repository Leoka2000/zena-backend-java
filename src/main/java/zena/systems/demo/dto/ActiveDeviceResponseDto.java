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

    // ✅ Latest sensor values
    private Float latestTemperature;
    private Float latestVoltage;
    private Float latestAccelX;
    private Float latestAccelY;
    private Float latestAccelZ;
    private Integer latestFreq1;
    private Integer latestFreq2;
    private Integer latestFreq3;
    private Integer latestFreq4;
    private Integer latestAmpl1;
    private Integer latestAmpl2;
    private Integer latestAmpl3;
    private Integer latestAmpl4;
}