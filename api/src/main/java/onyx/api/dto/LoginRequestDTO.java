package onyx.api.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String nombreUsuario;
    private String passwordHash;
}
