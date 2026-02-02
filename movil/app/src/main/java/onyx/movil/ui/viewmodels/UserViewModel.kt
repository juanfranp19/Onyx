package onyx.movil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import onyx.movil.providers.UserProvider
import onyx.movil.ui.states.UserUiState

class UserViewModel(private val provider: UserProvider) : ViewModel() {
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState

    fun login(nombreUsuario: String, passwordHash: String) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = UserUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.login(nombreUsuario, passwordHash)
                .onSuccess { user -> _uiState.value = UserUiState.SuccessLogin(user) }
                .onFailure { _ -> _uiState.value = UserUiState.Error("Error en el login") }
        }
    }

    fun getUsuario(id: Long?) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = UserUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.getUser(id)
                .onSuccess { user -> _uiState.value = UserUiState.SuccessGetUsuario(user) }
                .onFailure { _ -> _uiState.value = UserUiState.Error("Error en el login") }
        }
    }
}
