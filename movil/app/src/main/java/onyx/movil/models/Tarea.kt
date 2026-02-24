package onyx.movil.models

data class Tarea(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val fechaCreacion: String,
    val fechaVencimiento: String,
    val lista: String,
    val grupoId: Long,
    val creadorId: Long,
    val completada: Boolean
)
