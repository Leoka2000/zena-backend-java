package zena.systems.demo.service;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    public List<AppUser> allUsers() {
        List<AppUser> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

   
}