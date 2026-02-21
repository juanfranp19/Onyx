package onyx.movil.ui.states

import onyx.movil.models.Tarea

sealed class TareaUiState {
    object Idle: TareaUiState()
    object Loading: TareaUiState()
    object Empty: TareaUiState()
    object SuccessDeleteTarea: TareaUiState()
    data class SuccessGetTareasByGrupo(val tareas: List<Tarea>): TareaUiState()
    data class SuccessGetTarea(val tarea: Tarea): TareaUiState()
    data class SuccessPostTarea(val tarea: Tarea): TareaUiState()
    data class SuccessPutTarea(val tarea: Tarea): TareaUiState()
    data class Error(val message: String): TareaUiState()
}
