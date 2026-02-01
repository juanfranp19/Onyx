package onyx.movil.providers

import onyx.movil.models.Tarea
import onyx.movil.retrofit.OnyxAPI
import retrofit2.HttpException

class TareaProvider(private val api: OnyxAPI) {

    suspend fun getTareasByGrupo(grupoId: Long?): Result<List<Tarea>> {
        try {

            // llama a la api
            val tareas = api.getTareasByGrupo(grupoId)

            return when {
                tareas.isSuccessful ->
                     Result.success(tareas.body() ?: emptyList())
                tareas.code() == 404 ->
                    Result.success(emptyList())
                else ->
                    Result.failure(HttpException(tareas))
            }

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
