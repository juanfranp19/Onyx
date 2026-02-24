package onyx.movil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import onyx.movil.providers.GrupoProvider
import onyx.movil.ui.states.GrupoUiState

class GrupoViewModel(private val provider: GrupoProvider) : ViewModel() {
    private val _uiState = MutableStateFlow<GrupoUiState>(GrupoUiState.Idle)
    val uiState: StateFlow<GrupoUiState> = _uiState

    fun getGrupos(id: Long?) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = GrupoUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.getGrupos(id)
                .onSuccess { grupos ->
                    if (grupos.isEmpty()) _uiState.value = GrupoUiState.Empty
                    else _uiState.value = GrupoUiState.SuccessGetGrupos(grupos)
                }
                .onFailure { _ -> _uiState.value = GrupoUiState.Error("Error al cargar grupos") }
        }
    }

    fun getGrupo(id: Long?) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = GrupoUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.getGrupo(id)
                .onSuccess { grupo -> _uiState.value = GrupoUiState.SuccessGetGrupo(grupo) }
                .onFailure { _ -> _uiState.value = GrupoUiState.Error("Error al cargar grupo") }
        }
    }

    fun postGrupo(nombre: String, desc: String, creadorId: Long?) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = GrupoUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.postGrupo(nombre, desc, creadorId)
                .onSuccess { grupo ->
                    _uiState.value = GrupoUiState.SuccessPostGrupo(grupo)
                }
                .onFailure { _ -> _uiState.value = GrupoUiState.Error("Error al crear grupo") }
        }
    }

    fun deleteGrupo(grupoId: Long?) {
        viewModelScope.launch {
            // cambia estado
            _uiState.value = GrupoUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.deleteGrupo(grupoId)
                .onSuccess { _ -> _uiState.value = GrupoUiState.SuccessDeleteGrupo }
                .onFailure { _ -> _uiState.value = GrupoUiState.Error("Error al eliminar el grupo") }
        }
    }
}
