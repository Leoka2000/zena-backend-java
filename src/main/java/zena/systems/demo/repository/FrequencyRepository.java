package zena.systems.demo.repository;

import zena.systems.demo.model.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, Long> {
    List<Frequency> findAllByOrderByTimestampAsc();
    List<Frequency> findByTimestampGreaterThanEqualOrderByTimestampAsc(Long fromTimestamp);
    List<Frequency> findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(Long deviceId, Long fromTimestamp);
    List<Frequency> findByDevice_IdOrderByTimestampAsc(Long deviceId);
}