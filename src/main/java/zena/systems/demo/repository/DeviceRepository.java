package zena.systems.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zena.systems.demo.model.Device;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(Long userId);
}
