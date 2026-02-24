package onyx.api.dto;

import lombok.Data;

@Data
public class TareaRequestDTO {
    private String titulo;
    private String descripcion;
    private String fechaVencimiento;
    private Integer creador_id;
    private Integer grupo_id;
    private Boolean completada = false;
}
