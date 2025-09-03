package zena.systems.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "frequencies")
public class Frequency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer freq1;

    @Column(nullable = false)
    private Integer freq2;

    @Column(nullable = false)
    private Integer freq3;

    @Column(nullable = false)
    private Integer freq4;

    @Column(nullable = false)
    private Long timestamp; // UNIX timestamp from BLE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}