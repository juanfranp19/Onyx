package onyx.api.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "listas")
@Data

public class Lista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30, nullable = false)
    private String nombre;

    private Integer posicion;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;
}
