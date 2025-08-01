package zena.systems.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "accelerometers")
public class Accelerometer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private Float y;

    @Column(nullable = false)
    private Float z;

    @Column(nullable = false)
    private Long timestamp;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}