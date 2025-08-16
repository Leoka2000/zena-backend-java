package zena.systems.demo.repository;

import zena.systems.demo.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    List<Temperature> findAllByOrderByCreatedAtAsc();

    List<Temperature> findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(Instant fromCreatedAt);

    List<Temperature> findByDevice_IdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(Long deviceId,
            Instant fromCreatedAt);

    List<Temperature> findByDevice_IdOrderByCreatedAtAsc(Long deviceId);
}
