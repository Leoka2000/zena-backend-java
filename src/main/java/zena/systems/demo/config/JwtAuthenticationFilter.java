package zena.systems.demo.config;

import zena.systems.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.debug("=== JWT Authentication Filter Start ===");
        logger.debug("Processing request to: {}", request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authHeader);

        // Header check
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Bearer token found in Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            logger.debug("Extracted JWT token: {}", jwt.substring(0, Math.min(jwt.length(), 10)) + "..."); // Log first 10 chars

            final String userEmail = jwtService.extractUsername(jwt);
            logger.debug("Extracted user email from token: {}", userEmail);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.debug("Existing authentication in context: {}", authentication);

            if (userEmail != null && authentication == null) {
                logger.debug("Attempting to load user details for: {}", userEmail);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                logger.debug("Loaded user details: {}", userDetails.getUsername());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    logger.debug("Token is valid for user: {}", userDetails.getUsername());
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    logger.debug("Created authentication token with authorities: {}", userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Successfully authenticated user: {}", userDetails.getUsername());
                } else {
                    logger.warn("Token validation failed for user: {}", userDetails.getUsername());
                }
            } else if (authentication != null) {
                logger.debug("User already authenticated: {}", authentication.getName());
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            logger.error("JWT Authentication failed", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        } finally {
            logger.debug("=== JWT Authentication Filter End ===");
        }
    }
}