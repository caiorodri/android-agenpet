package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.model.usuario.LoginRequest
import br.com.caiorodri.agenpet.model.usuario.LoginResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateSenhaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UsuarioService {

    @POST("usuarios/autenticar")
    suspend fun autenticar(@Body usuario: LoginRequest): Response<LoginResponse>

    @POST("usuarios")
    suspend fun salvar(@Body usuario: UsuarioRequest): Response<UsuarioResponse>

    @PUT("usuarios")
    suspend fun atualizar(@Body usuario: UsuarioUpdateRequest): Response<UsuarioResponse>

    @PUT("usuarios/atualizar-senha")
    suspend fun atualizarsenha(@Body usuario: UsuarioUpdateSenhaRequest): Response<UsuarioResponse>

    @GET("usuarios/me")
    suspend fun getMeuPerfil(): Response<UsuarioResponse>

}