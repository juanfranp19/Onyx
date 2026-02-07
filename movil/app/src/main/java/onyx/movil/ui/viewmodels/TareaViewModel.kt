package onyx.movil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import onyx.movil.providers.TareaProvider
import onyx.movil.ui.states.TareaUiState

class TareaViewModel(private val provider: TareaProvider) : ViewModel() {
    private val _uiState = MutableStateFlow<TareaUiState>(TareaUiState.Idle)
    val uiState: StateFlow<TareaUiState> = _uiState

    fun getTareasByGrupo(grupoId: Long?) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = TareaUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.getTareasByGrupo(grupoId)
                .onSuccess { tareas ->
                    // vacío
                    if (tareas.isEmpty()) _uiState.value = TareaUiState.Empty
                    // con datos
                    else _uiState.value = TareaUiState.SuccessGetTareasByGrupo(tareas)
                }
                .onFailure { _ -> _uiState.value = TareaUiState.Error("Error al cargar las tareas por grupo") }
        }
    }

    fun postTarea(titulo: String?, descripcion: String?, creadorId: Long?, grupoId: Long?) {
        viewModelScope.launch {
            // cambia el estado
            _uiState.value = TareaUiState.Loading

            // llama al la función del provider y cambia de estado
            provider.postTarea(titulo, descripcion, creadorId, grupoId)
                .onSuccess { tarea ->
                    _uiState.value = TareaUiState.SuccessPostTarea(tarea)
                }
                .onFailure { _ -> _uiState.value = TareaUiState.Error("Error al crear la tarea") }
        }
    }
}
