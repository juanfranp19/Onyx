package onyx.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GrupoRequestDTO {
    private String nombre;
    private String descripcion;
    private List<Integer> usuariosId;
    
    @JsonProperty("creadorId")
    private Integer creadorId;
}
