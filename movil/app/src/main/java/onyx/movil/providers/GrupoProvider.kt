package onyx.movil.providers

import android.util.Log
import onyx.movil.models.Grupo
import onyx.movil.retrofit.OnyxAPI
import retrofit2.HttpException

class GrupoProvider(private val api: OnyxAPI) {

    suspend fun getGrupos(id: Long?): Result<List<Grupo>> {
        try {

            // llama a la api
            val grupos = api.getGrupos(id)

            // obtiene respuesta de la api
            return when {
                grupos.isSuccessful ->
                    Result.success(grupos.body() ?: emptyList())
                grupos.code() == 404 ->
                    Result.success(emptyList())
                else ->
                    Result.failure(HttpException(grupos))
            }

        } catch (e: Exception) {
            Log.e("GET_GRUPOS_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun getGrupo(id: Long?): Result<Grupo> {
        try {

            // llama a la api
            val grupo = api.getGrupo(id)

            // obtiene respuesta de la api
            return Result.success(grupo)

        } catch (e: Exception) {
            Log.e("GET_GRUPO_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun postGrupo(nombre: String, desc: String, creadorId: Long?): Result<Grupo> {
        try {

            // llama a la api
            val grupo = api.postGrupo(
                mapOf(
                    "nombre" to nombre,
                    "descripcion" to desc,
                    "creadorId" to creadorId
                ) as Map<String, @JvmSuppressWildcards Any>
            )

            // obtiene respuesta de la api
            return Result.success(grupo)

        } catch (e: Exception) {
            Log.e("POST_GRUPO_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun deleteGrupo(grupoId: Long?): Result<Unit> {
        try {

            api.deleteGrupo(grupoId)
            return Result.success(Unit)

        } catch (e: Exception) {
            Log.e("DELETE_GRUPO_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }
}
