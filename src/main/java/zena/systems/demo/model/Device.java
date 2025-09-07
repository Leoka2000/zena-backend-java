package zena.systems.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "devices")
@Getter
@Setter
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String serviceUuid;

    @Column(nullable = true)
    private String measurementCharUuid;

    @Column(nullable = true)
    private String logReadCharUuid;

    @Column(nullable = true)
    private String setTimeCharUuid;

    @Column(nullable = true)
    private String ledControlCharUuid;

    @Column(nullable = true)
    private String sleepControlCharUuid;

    @Column(nullable = true)
    private String alarmCharUuid;

    @Column(name = "last_received_timestamp", nullable = true)
    private Long lastReceivedTimestamp; 

    @Column(name = "isRegisteredDevice", nullable = false)
    private boolean isRegisteredDevice = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();


     // === Tracking fields ===
  
    @Column(name = "latest_temperature")
    private Float latestTemperature;

    @Column(name = "latest_voltage")
    private Float latestVoltage;

    // accelerometer (x,y,z)
    @Column(name = "latest_accel_x")
    private Float latestAccelX;

    @Column(name = "latest_accel_y")
    private Float latestAccelY;

    @Column(name = "latest_accel_z")
    private Float latestAccelZ;

    // frequencies
    @Column(name = "latest_freq1")
    private Integer latestFreq1;

    @Column(name = "latest_freq2")
    private Integer latestFreq2;

    @Column(name = "latest_freq3")
    private Integer latestFreq3;

    @Column(name = "latest_freq4")
    private Integer latestFreq4;

    // amplitudes
    @Column(name = "latest_ampl1")
    private Integer latestAmpl1;

    @Column(name = "latest_ampl2")
    private Integer latestAmpl2;

    @Column(name = "latest_ampl3")
    private Integer latestAmpl3;

    @Column(name = "latest_ampl4")
    private Integer latestAmpl4;
}
