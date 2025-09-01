package zena.systems.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zena.systems.demo.model.Temperature;

import java.util.List;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    List<Temperature> findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(Long deviceId, Long from);
    List<Temperature> findByDevice_IdOrderByTimestampAsc(Long deviceId);
    List<Temperature> findByTimestampGreaterThanEqualOrderByTimestampAsc(Long from);
    List<Temperature> findAllByOrderByTimestampAsc();
}
