package zena.systems.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.UserRepository;
import zena.systems.demo.utils.JwtTokenUtil;

@RestController
public class RegistrationController {

    @Autowired
    private UserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Response DTO
    public static class RegistrationResponse {
        public String message;
        public String token;

        public RegistrationResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }
    }

    @PostMapping(value = "/req/signup", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody AppUser user) {

        AppUser existingAppUser = myAppUserRepository.findByEmail(user.getEmail());

        if (existingAppUser != null) {
            return new ResponseEntity<>("User already exists.", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        myAppUserRepository.save(user);

        String token = jwtTokenUtil.generateToken(user.getEmail());
        RegistrationResponse response = new RegistrationResponse("Registration successful", token);

        return ResponseEntity.ok(response);
    }
}
