package onyx.movil.retrofit

import onyx.movil.models.Grupo
import onyx.movil.models.Tarea
import onyx.movil.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OnyxAPI {

    // ─────────────────────────────────────────
    //  USUARIOS
    // ─────────────────────────────────────────

    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): User

    @POST("usuarios")
    suspend fun register(@Body body: Map<String, String>): User

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long?): User

    @GET("usuarios/username/{username}")
    suspend fun getUsuarioByUsername(@Path("username") username: String?): User

    @PUT("usuarios/{id}")
    suspend fun putUser(@Path("id") userId: Long?, @Body body: Map<String, String>): User

    // ─────────────────────────────────────────
    //  GRUPOS
    // ─────────────────────────────────────────

    @GET("grupos/{grupoId}")
    suspend fun getGrupo(@Path("grupoId") grupoId: Long?): Grupo

    @GET("grupos/usuario/{id}")
    suspend fun getGrupos(@Path("id") id: Long?): Response<List<Grupo>>

    @POST("grupos")
    suspend fun postGrupo(@Body body: Map<String, @JvmSuppressWildcards Any>): Grupo

    @DELETE("grupos/{grupoId}")
    suspend fun deleteGrupo(@Path("grupoId") grupoId: Long?)

    // ─────────────────────────────────────────
    //  TAREAS
    // ─────────────────────────────────────────

    @GET("tareas/{tareaId}")
    suspend fun getTarea(@Path("tareaId") tareaId: Long?): Tarea

    @GET("tareas/grupo/{grupoId}")
    suspend fun getTareasByGrupo(@Path("grupoId") grupoId: Long?): Response<List<Tarea>>

    @POST("tareas")
    suspend fun postTarea(@Body body: Map<String, @JvmSuppressWildcards Any>): Tarea

    @PUT("tareas/{id}")
    suspend fun putTarea(@Path("id") tareaId: Long?, @Body body: Map<String, @JvmSuppressWildcards Any>): Tarea

    @DELETE("tareas/{tareaId}")
    suspend fun deleteTarea(@Path("tareaId") tareaId: Long?)
}
