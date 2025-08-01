package zena.systems.demo.repository;

import zena.systems.demo.model.Accelerometer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccelerometerRepository extends JpaRepository<Accelerometer, Long> {
    List<Accelerometer> findByTimestampGreaterThanEqualOrderByTimestamp(Long fromTimestamp);
     List<Accelerometer> findAllByOrderByTimestampAsc();
}