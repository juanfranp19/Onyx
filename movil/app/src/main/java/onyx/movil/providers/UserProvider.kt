package onyx.movil.providers

import android.util.Log
import onyx.movil.models.User
import onyx.movil.retrofit.OnyxAPI

class UserProvider(private val api: OnyxAPI) {

    suspend fun login(nombreUsuario: String, passwordHash: String): Result<User> {
        try {

            // llama a la api
            val user: User = api.login(
                mapOf(
                    "nombreUsuario" to nombreUsuario,
                    "passwordHash" to passwordHash
                )
            )

            // obtiene el usuario loggeado
            return Result.success(user)

        } catch (e: Exception) {
            Log.e("LOGIN_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun getUser(id: Long?): Result<User> {
        try {

            // llama a la api
            val user: User = api.getUsuario(id)

            // obtiene los datos del usuario
            return Result.success(user)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
