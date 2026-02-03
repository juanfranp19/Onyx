package onyx.movil.providers

import android.util.Log
import onyx.movil.models.Grupo
import onyx.movil.retrofit.OnyxAPI

class GrupoProvider(private val api: OnyxAPI) {

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
}
