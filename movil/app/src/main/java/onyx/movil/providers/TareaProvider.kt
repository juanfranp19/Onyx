package onyx.movil.providers

import android.util.Log
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

    suspend fun getTarea(tareaId: Long?): Result<Tarea> {
        try {

            //llama a la api
            val tarea = api.getTarea(tareaId)

            // obtiene la tarea
            return Result.success(tarea)

        } catch (e: Exception) {
            Log.e("GET_TAREA_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun postTarea(titulo: String?, descripcion: String?, fechaVenc: String?, creadorId: Long?, grupoId: Long?): Result<Tarea> {
        try {

            // llama a la api
            val tarea = api.postTarea(
                mapOf(
                    "titulo" to titulo,
                    "descripcion" to descripcion,
                    "fechaVencimiento" to fechaVenc,
                    "creador_id" to creadorId,
                    "grupo_id" to grupoId
                ) as Map<String, @JvmSuppressWildcards Any>
            )

            // obtiene respuesta de la api
            return Result.success(tarea)

        } catch (e: Exception) {
            Log.e("POST_TAREA_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun putTarea(tareaId: Long?, titulo: String?, descripcion: String?, fechaVenc: String?, grupoId: Long?): Result<Tarea> {
        try {

            // llama a la api
            val tarea = api.putTarea(
                tareaId,
                mapOf(
                    "titulo" to titulo,
                    "descripcion" to descripcion,
                    "fechaVencimiento" to fechaVenc,
                    "grupo_id" to grupoId
                ) as Map<String, @JvmSuppressWildcards Any>
            )

            // obtiene respuesta de la api
            return Result.success(tarea)

        } catch (e: Exception) {
            Log.e("PUT_TAREA_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun putTareaCompletada(tareaId: Long?, completada: Boolean?): Result<Tarea> {
        try {

            val tarea = api.putTareaCompletada(tareaId, completada)
            return Result.success(tarea)

        } catch (e: Exception) {
            Log.e("PUT_TAREA_COMPLETADA_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun deleteTarea(tareaId: Long?): Result<Unit> {
        try {

            api.deleteTarea(tareaId)
            return Result.success(Unit)

        } catch (e: Exception) {
            Log.e("DELETE_TAREA_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }
}
