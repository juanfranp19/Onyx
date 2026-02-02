package onyx.movil.models

data class Grupo(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val fechaCreacion: String,
    val creadorId: Long,
    val tareas: List<Tarea>
)
