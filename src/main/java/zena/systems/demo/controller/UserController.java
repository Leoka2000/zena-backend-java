package zena.systems.demo.controller;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.dto.AuthedUserDto;
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
public ResponseEntity<AuthedUserDto> authenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("Authentication object: " + authentication);

    Object principal = authentication.getPrincipal();
    System.out.println("Principal class: " + principal.getClass().getName());
    System.out.println("Principal content: " + principal.toString());

    if (principal instanceof AppUser currentUser) {
        AuthedUserDto dto = new AuthedUserDto(currentUser.getUsername(), currentUser.getEmail());
        System.out.println("Returning AuthedUserDto: " + dto.getUsername() + ", " + dto.getEmail());
        return ResponseEntity.ok(dto);
    } else {
        System.out.println("Principal is not an instance of AppUser");
        return ResponseEntity.status(401).build();
    }
}

    @GetMapping("/")
    public ResponseEntity<List<AppUser>> allUsers() {
        List<AppUser> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }
}
