package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDeviceDto {
    
    private String serviceUuid;
    private String measurementCharUuid;
    private String logReadCharUuid;
    private String setTimeCharUuid;
    private String ledControlCharUuid;
    private String sleepControlCharUuid;
    private String alarmCharUuid;
}
