package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.api.model.PageResponse
import br.com.caiorodri.agenpet.model.usuario.Estado
import br.com.caiorodri.agenpet.model.usuario.LoginRequest
import br.com.caiorodri.agenpet.model.usuario.LoginResponse
import br.com.caiorodri.agenpet.model.usuario.Status
import br.com.caiorodri.agenpet.model.usuario.UsuarioAlterarSenha
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioService {

    @POST("usuarios/autenticar")
    suspend fun autenticar(@Body usuario: LoginRequest): Response<LoginResponse>

    @GET("usuarios/me")
    suspend fun getMeuPerfil(): Response<UsuarioResponse>

    @GET("usuarios")
    suspend fun listar(
        @Query("pagina") pagina: Int,
        @Query("quantidadeItens") quantidadeItens: Int
    ): Response<PageResponse<UsuarioResponse>>

    @GET("usuarios/{id}")
    suspend fun recuperar(@Path("id") id: Long): Response<UsuarioResponse>

    @GET("usuarios/email/{email}")
    suspend fun recuperarByEmail(@Path("email") email: String): Response<UsuarioResponse>

    @POST("usuarios")
    suspend fun salvar(@Body usuario: UsuarioRequest): Response<UsuarioResponse>

    @PUT("usuarios")
    suspend fun atualizar(@Body usuario: UsuarioUpdateRequest): Response<LoginResponse>

    @DELETE("usuarios/{id}")
    suspend fun deletar(@Path("id") id: Long): Response<Void>

    @PUT("usuarios/alterar-senha")
    suspend fun alterarSenha(@Body request: UsuarioAlterarSenha): Response<Void>

    @GET("usuarios/recuperar-senha/{email}")
    suspend fun enviarCodigoRecuperacao(@Path("email") email: String): Response<Void>

    @GET("usuarios/{id}/validar-codigo/{codigo}")
    suspend fun validarCodigoRecuperacao(
        @Path("id") id: Long,
        @Path("codigo") codigo: String
    ): Response<Void>

    @GET("usuarios/status")
    suspend fun listarStatus(): Response<List<Status>>

    @GET("usuarios/clientes")
    suspend fun listarClientes(
        @Query("pagina") pagina: Int,
        @Query("quantidadeItens") quantidadeItens: Int
    ): Response<PageResponse<UsuarioResponse>>

    @GET("usuarios/recepcionistas")
    suspend fun listarRecepcionistas(): Response<List<UsuarioResponse>>

    @GET("usuarios/veterinarios")
    suspend fun listarVeterinarios(): Response<List<UsuarioResponse>>

    @GET("usuarios/funcionarios")
    suspend fun listarFuncionarios(
        @Query("pagina") pagina: Int,
        @Query("quantidadeItens") quantidadeItens: Int
    ): Response<PageResponse<UsuarioResponse>>

    @GET("usuarios/funcionarios/todos")
    suspend fun listarFuncionariosTodos(): Response<List<UsuarioResponse>>

    @GET("usuarios/recepcionista/auto-atendimento")
    suspend fun recuperarRecepcionistaAutoAtendimento(): Response<UsuarioResponse>

    @GET("usuarios/estados")
    suspend fun listarEstados(): Response<List<Estado>>
}