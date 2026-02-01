package onyx.movil.providers

import onyx.movil.models.Grupo
import onyx.movil.retrofit.OnyxAPI

class GrupoProvider(private val api: OnyxAPI) {

    suspend fun getGrupos(id: Long?): Result<List<Grupo>> {
        try {

            // llama a la api
            val grupos = api.getGrupos(id)

            return Result.success(grupos)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
