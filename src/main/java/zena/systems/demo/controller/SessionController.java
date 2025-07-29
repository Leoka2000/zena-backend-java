package zena.systems.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("isNewSession", session.isNew());
        response.put("creationTime", session.getCreationTime());
        response.put("lastAccessedTime", session.getLastAccessedTime());
        response.put("maxInactiveInterval", session.getMaxInactiveInterval());
        
        if (authentication != null) {
            response.put("authenticated", authentication.isAuthenticated());
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
        } else {
            response.put("authenticated", false);
        }

        logger.info("Session check - ID: {}, Authenticated: {}", 
            session.getId(), 
            authentication != null && authentication.isAuthenticated());
            
        return ResponseEntity.ok(response);
    }
}