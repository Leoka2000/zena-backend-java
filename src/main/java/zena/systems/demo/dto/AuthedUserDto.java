package zena.systems.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthedUserDto {
    private String username;
    private String email;

    public AuthedUserDto(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
