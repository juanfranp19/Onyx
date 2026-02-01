package onyx.movil.models

data class Grupo(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val fechaCreacion: String,
    val creador: User
)
