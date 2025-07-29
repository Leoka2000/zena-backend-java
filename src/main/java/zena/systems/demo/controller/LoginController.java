package zena.systems.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import zena.systems.demo.model.AppUser;
import zena.systems.demo.model.UserRepository;
import zena.systems.demo.utils.JwtTokenUtil;

@RestController
public class LoginController {

    @Autowired
    private UserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class LoginResponse {
        public String token;
        public LoginResponse(String token) {
            this.token = token;
        }
    }

    @PostMapping(value = "/req/login", consumes = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        AppUser user = myAppUserRepository.findByEmail(loginRequest.email);

        if (user == null || !passwordEncoder.matches(loginRequest.password, user.getPassword())) {
            return new ResponseEntity<>("Invalid email or password.", HttpStatus.UNAUTHORIZED);
        }

        String token = JwtTokenUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
