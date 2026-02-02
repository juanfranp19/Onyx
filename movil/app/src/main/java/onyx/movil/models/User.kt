package onyx.movil.models

data class User(
    val id: Long,
    val nombreUsuario: String,
    val email: String,
    val passwordHash: String,
    val fechaRegistro: String,
    val grupos: List<Grupo>
)
