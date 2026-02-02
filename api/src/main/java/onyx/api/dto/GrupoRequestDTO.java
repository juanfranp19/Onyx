package onyx.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GrupoRequestDTO {
    private String nombre;
    private String descripcion;
    
    @JsonProperty("creadorId")
    private Integer creadorId;
}
