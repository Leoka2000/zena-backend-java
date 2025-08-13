package zena.systems.demo.controller;

import zena.systems.demo.service.JwtService;
import zena.systems.demo.dto.UpdateUserDto;
import zena.systems.demo.dto.UserDeviceStatusDto;
import zena.systems.demo.model.AppUser;
import zena.systems.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import zena.systems.demo.dto.ChangePasswordDto;

import org.springframework.http.HttpHeaders;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtService jwtService; // Correct type

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService; // Proper initialization
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UpdateUserDto updateDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            AppUser currentUser = (AppUser) authentication.getPrincipal();
            

            AppUser updatedUser = userService.updateUser(
                    currentUser.getId(),
                    updateDto.getUsername(),
                    updateDto.getEmail());

            // Now jwtService is properly available
            String newToken = jwtService.generateToken(updatedUser);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + newToken)
                    .body(Map.of(
                            "user", updatedUser,
                            "token", newToken));
        } catch (ClassCastException e) {
            logger.error("Authentication principal type mismatch", e);
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            logger.error("Error updating user", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to update user"));
        }
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
                authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null");
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

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            AppUser currentUser = (AppUser) authentication.getPrincipal();

            userService.changePassword(
                    currentUser.getId(),
                    dto.getCurrentPassword(),
                    dto.getNewPassword(),
                    dto.getConfirmPassword());

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during password change", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to change password"));
        }
    }

    @GetMapping("/me/device-status")
    public ResponseEntity<UserDeviceStatusDto> getDeviceCreationStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            boolean status = userService.hasUserCreatedFirstDevice(currentUser.getId());
            return ResponseEntity.ok(new UserDeviceStatusDto(status));
        } catch (ClassCastException e) {
            logger.error("Authentication principal type mismatch", e);
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            logger.error("Error retrieving device status", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}