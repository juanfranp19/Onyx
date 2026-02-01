package onyx.movil.providers

import onyx.movil.models.Grupo
import onyx.movil.retrofit.OnyxAPI
import retrofit2.HttpException

class GrupoProvider(private val api: OnyxAPI) {

    suspend fun getGrupos(id: Long?): Result<List<Grupo>> {
        try {

            // llama a la api
            val grupos = api.getGrupos(id)

            return when {
                grupos.isSuccessful ->
                    Result.success(grupos.body() ?: emptyList())
                grupos.code() == 404 ->
                    Result.success(emptyList())
                else ->
                    Result.failure(HttpException(grupos))
            }

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
