package zena.systems.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "amplitudes")
public class Amplitude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ampl1;

    @Column(nullable = false)
    private Integer ampl2;

    @Column(nullable = false)
    private Integer ampl3;

    @Column(nullable = false)
    private Integer ampl4;

    @Column(nullable = false)
    private Long timestamp; // UNIX timestamp from BLE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}