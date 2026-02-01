package onyx.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TareaRequestDTO {
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaVencimiento;
    private Integer creador_id;
    private Integer grupo_id;
}
