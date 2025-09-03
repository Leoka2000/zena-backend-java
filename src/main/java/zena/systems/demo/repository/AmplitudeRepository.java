package zena.systems.demo.repository;

import zena.systems.demo.model.Amplitude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmplitudeRepository extends JpaRepository<Amplitude, Long> {
    List<Amplitude> findAllByOrderByTimestampAsc();
    List<Amplitude> findByTimestampGreaterThanEqualOrderByTimestampAsc(Long fromTimestamp);
    List<Amplitude> findByDevice_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(Long deviceId, Long fromTimestamp);
    List<Amplitude> findByDevice_IdOrderByTimestampAsc(Long deviceId);
}