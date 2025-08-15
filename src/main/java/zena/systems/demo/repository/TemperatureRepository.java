package zena.systems.demo.repository;

import zena.systems.demo.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    List<Temperature> findAllByOrderByTimestampAsc();  // Unfiltered full history
    List<Temperature> findByTimestampGreaterThanEqualOrderByTimestampAsc(Long fromTimestamp);  // Filtered


    List<Temperature> findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(Long deviceId, Long fromTimestamp);

    List<Temperature> findByDevice_IdOrderByTimestampAsc(Long deviceId);
   
}
