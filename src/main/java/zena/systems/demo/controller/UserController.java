package zena.systems.demo.controller;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<AppUser> authenticatedUser() {
        logger.info("Entering authenticatedUser() endpoint");
        
        // Log the security context state
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            logger.error("Authentication object is null - no authentication information available");
            return ResponseEntity.status(401).build();
        }
        
        logger.debug("Authentication object: {}", authentication);
        logger.debug("Authentication principal class: {}", 
            authentication.getPrincipal() != null ? 
            authentication.getPrincipal().getClass().getName() : "null");
        logger.debug("Authentication name: {}", authentication.getName());
        logger.debug("Authentication authorities: {}", authentication.getAuthorities());
        logger.debug("Authentication is authenticated: {}", authentication.isAuthenticated());
        
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            logger.info("Successfully retrieved authenticated user: {}", currentUser.getUsername());
            logger.debug("Full user details: {}", currentUser);
            
            return ResponseEntity.ok(currentUser);
        } catch (ClassCastException e) {
            logger.error("Principal is not of type AppUser. Actual type: {}", 
                authentication.getPrincipal().getClass().getName(), e);
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving authenticated user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<AppUser>> allUsers() {
        logger.info("Fetching all users");
        List<AppUser> users = userService.allUsers();
        logger.debug("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }
}