package onyx.movil.ui.states

import onyx.movil.models.User

sealed class UserUiState {
    object Idle: UserUiState()
    object Loading: UserUiState()
    data class SuccessLogin(val user: User): UserUiState()
    data class SuccessRegister(val user: User): UserUiState()
    data class SuccessPutUser(val user: User): UserUiState()
    data class SuccessGetUsuario(val usuario: User): UserUiState()
    data class Error(val message: String): UserUiState()
}
