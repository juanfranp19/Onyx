package onyx.movil.retrofit

import onyx.movil.models.Tarea
import onyx.movil.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OnyxAPI {
    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): User

    @POST("usuarios")
    suspend fun register(@Body body: Map<String, String>): User

    @GET("tareas/grupo/{grupoId}")
    suspend fun getTareasByGrupo(@Path("grupoId") grupoId: Long?): Response<List<Tarea>>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long?): User
}
