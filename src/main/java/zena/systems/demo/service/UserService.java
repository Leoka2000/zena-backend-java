package zena.systems.demo.service;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AppUser> allUsers() {
        List<AppUser> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Transactional
    public AppUser updateUser(Long userId, String username, String email) {
      AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (username != null && !username.isBlank()) {
            // Check if username is already taken by another user
            Optional<AppUser> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(username);
        }
        
        if (email != null && !email.isBlank()) {
            // Check if email is already taken by another user
            Optional<AppUser> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(email);
        }
        
       return userRepository.save(user);
    }


    
}


