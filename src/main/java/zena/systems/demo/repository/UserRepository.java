package zena.systems.demo.repository;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.Temperature;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByVerificationCode(String verificationCode);



}
