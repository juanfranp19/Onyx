package onyx.movil.retrofit

import onyx.movil.models.Grupo
import onyx.movil.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OnyxAPI {
    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): User

    @GET("grupos/usuario/{id}")
    suspend fun getGrupos(@Path("id") id: Long?): List<Grupo>
}
