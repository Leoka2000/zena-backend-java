package zena.systems.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication, HttpServletRequest request) {
        try {
            logger.info("Attempting to fetch user for: " + authentication.getName());
            
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Unauthenticated request to /api/users/me");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
            }
            
            AppUser user = userRepository.findByEmail(authentication.getName());
            
            if (user == null) {
                logger.error("User not found for email: " + authentication.getName());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            
            // Return only necessary fields (don't expose password/tokens)
            Map<String, String> userData = new HashMap<>();
            userData.put("id", user.getId().toString());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            logger.error("Error in /api/users/me", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }
}