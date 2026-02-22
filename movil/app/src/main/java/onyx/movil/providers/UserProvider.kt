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

    suspend fun register(nombreUsuario: String, email: String, passwordHash: String): Result<User> {
        try {

            // llama a la api
            val user: User = api.register(
                mapOf(
                    "nombreUsuario" to nombreUsuario,
                    "email" to email,
                    "passwordHash" to passwordHash
                )
            )

            // obtiene el usuario loggeado
            return Result.success(user)

        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "Exception: ", e)
            return Result.failure(e)
        }
    }

    suspend fun putUser(userId: Long?, nombreUsuario: String, email: String, password: String): Result<User> {
        try {

            // llama a la api
            val user: User = api.putUser(
                userId,
                mapOf(
                    "nombreUsuario" to nombreUsuario,
                    "email" to email,
                    "passwordHash" to password
                )
            )

            // obtiene el usuario loggeado
            return Result.success(user)

        } catch (e: Exception) {
            Log.e("PUT_USER_ERROR", "Exception: ", e)
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
