package onyx.api.dto;

import lombok.Data;

@Data
public class GrupoRequestDTO {
    private String nombre;
    private String descripcion;
    private Integer creador_id;
}
