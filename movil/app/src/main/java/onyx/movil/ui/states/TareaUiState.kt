package onyx.movil.ui.states

import onyx.movil.models.Tarea

sealed class TareaUiState {
    object Idle: TareaUiState()
    object Loading: TareaUiState()
    object Empty: TareaUiState()
    data class SuccessGetTareasByGrupo(val tareas: List<Tarea>): TareaUiState()
    data class Error(val message: String): TareaUiState()
}
