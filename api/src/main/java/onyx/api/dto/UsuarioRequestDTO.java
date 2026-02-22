package onyx.api.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nombreUsuario;
    private String email;
    private String passwordHash;
}
