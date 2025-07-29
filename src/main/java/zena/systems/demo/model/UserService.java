package zena.systems.demo.model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor 
public class UserService implements UserDetailsService {
    
    private final UserRepository repository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        
        return User.builder()
                .username(user.getEmail()) // Using email as username
                .password(user.getPassword())
                .authorities("ROLE_USER") // Add appropriate authorities
                .build();
    }

    
}