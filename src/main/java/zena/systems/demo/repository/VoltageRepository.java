package zena.systems.demo.repository;

import zena.systems.demo.model.Voltage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface VoltageRepository extends JpaRepository<Voltage, Long> {
    List<Voltage> findAllByOrderByCreatedAtAsc();

    List<Voltage> findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(Instant fromTimestamp);
}
