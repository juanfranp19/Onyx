package onyx.movil.ui.states

import onyx.movil.models.Grupo

sealed class GrupoUiState {
    object Idle: GrupoUiState()
    object Loading: GrupoUiState()
    object Empty: GrupoUiState()
    data class SuccessGetGrupos(val grupos: List<Grupo>): GrupoUiState()
    data class SuccessGetGrupo(val grupo: Grupo): GrupoUiState()
    data class SuccessPostGrupo(val grupo: Grupo): GrupoUiState()
    data class Error(val message: String): GrupoUiState()
}
