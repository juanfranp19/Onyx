package onyx.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "miembros_grupo")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class MiembroGrupo {
    @EmbeddedId
    private MiembroGrupoId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @MapsId("grupoId")
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(length = 16, nullable = false)
    private String rol = "Invitado";

    @Column(name = "fecha_union", nullable = false)
    private LocalDateTime fechaUnion;

    @PrePersist
    protected void onCreate() {
        this.fechaUnion = LocalDateTime.now();
    }
}
