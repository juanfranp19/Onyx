package onyx.movil.retrofit

import onyx.movil.models.User
import retrofit2.http.Body
import retrofit2.http.POST

interface OnyxAPI {
    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): User
}
