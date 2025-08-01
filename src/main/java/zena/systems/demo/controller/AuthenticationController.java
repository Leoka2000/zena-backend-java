    package zena.systems.demo.controller;

    import zena.systems.demo.dto.LoginResponse;
import zena.systems.demo.dto.LoginUserDto;
    import zena.systems.demo.dto.RegisterUserDto;
    import zena.systems.demo.dto.VerifyUserDto;
    import zena.systems.demo.model.AppUser;
import zena.systems.demo.service.AuthenticationService;
    import zena.systems.demo.service.JwtService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;


    @RequestMapping("/auth")
    @RestController
    public class AuthenticationController {
        private final JwtService jwtService;

        private final AuthenticationService authenticationService;

        public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
            this.jwtService = jwtService;
            this.authenticationService = authenticationService;
        }

        @PostMapping("/signup")
        public ResponseEntity<AppUser> register(@RequestBody RegisterUserDto registerUserDto) {
            AppUser registeredUser = authenticationService.signup(registerUserDto);
            return ResponseEntity.ok(registeredUser);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
            AppUser authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        }

        

        @PostMapping("/verify")
        public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
            try {
                authenticationService.verifyUser(verifyUserDto);
                return ResponseEntity.ok("Account verified successfully");
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @PostMapping("/resend")
        public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
            try {
                authenticationService.resendVerificationCode(email);
                return ResponseEntity.ok("Verification code sent");
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
       
        @PostMapping("/logout")
        public ResponseEntity<String> logout() {
            // Simple response - in JWT systems, logout is primarily client-side
            return ResponseEntity.ok("Logged out successfully");
        }
    }