package onyx.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
@Data
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    private LocalDateTime fechaVencimiento;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @JsonProperty("grupoId")
    public Integer getGrupoId() {
        return grupo != null ? grupo.getId() : null;
    }

    @JsonProperty("creadorId")
    public Integer getCreadorId() {
        return creador != null ? creador.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
