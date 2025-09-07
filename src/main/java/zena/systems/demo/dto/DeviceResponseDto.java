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

    // latest readings
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