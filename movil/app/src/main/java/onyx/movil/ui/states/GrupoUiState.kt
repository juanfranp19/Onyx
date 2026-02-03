package onyx.movil.ui.states

import onyx.movil.models.Grupo

sealed class GrupoUiState {
    object Idle: GrupoUiState()
    object Loading: GrupoUiState()
    data class SuccessPostGrupo(val grupo: Grupo): GrupoUiState()
    data class Error(val message: String): GrupoUiState()
}
