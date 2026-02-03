package onyx.api.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String nombreUsuario;
    private String email;
    private String passwordHash;
}
