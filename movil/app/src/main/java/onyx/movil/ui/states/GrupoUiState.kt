package onyx.movil.ui.states

import onyx.movil.models.Grupo

sealed class GrupoUiState {
    object Idle: GrupoUiState()
    object Loading: GrupoUiState()
    data class SuccessGetGrupos(val grupos: List<Grupo>): GrupoUiState()
    data class Error(val message: String): GrupoUiState()
}
