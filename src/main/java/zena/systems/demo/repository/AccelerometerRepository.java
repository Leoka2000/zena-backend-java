package zena.systems.demo.repository;

import zena.systems.demo.model.Accelerometer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface AccelerometerRepository extends JpaRepository<Accelerometer, Long> {
    List<Accelerometer> findAllByOrderByCreatedAtAsc();
    List<Accelerometer> findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(Instant fromTimestamp);
    List<Accelerometer> findByDevice_IdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(Long deviceId, Instant fromCreatedAt);
    List<Accelerometer> findByDevice_IdOrderByCreatedAtAsc(Long deviceId);
}