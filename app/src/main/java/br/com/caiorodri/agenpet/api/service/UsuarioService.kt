package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.model.usuario.LoginRequest
import br.com.caiorodri.agenpet.model.usuario.LoginResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsuarioService {

    @POST("usuarios/autenticar")
    suspend fun autenticar(@Body usuario: LoginRequest): Response<LoginResponse>

    @POST("usuarios")
    suspend fun salvar(@Body usuario: UsuarioRequest): Response<UsuarioResponse>

    @GET("usuarios/me")
    suspend fun getMeuPerfil(): Response<UsuarioResponse>

}