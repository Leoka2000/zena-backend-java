package zena.systems.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zena.systems.demo.model.ActiveDevice;
import zena.systems.demo.model.AppUser;

public interface ActiveDeviceRepository extends JpaRepository<ActiveDevice, Long> {
    ActiveDevice findByUser(AppUser user);
    void deleteByUser(AppUser user);
    boolean existsByUser(AppUser user);
}