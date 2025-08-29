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

    @Column(name = "isRegisteredDevice", nullable = false)
    private boolean isRegisteredDevice = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
