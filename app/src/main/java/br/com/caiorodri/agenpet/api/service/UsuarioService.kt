package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.model.usuario.UsuarioLogin
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioService {

    @POST("usuarios/autenticar")
    suspend fun findByEmailAndSenha(@Body usuario: UsuarioLogin): Response<UsuarioResponse>

    @POST("usuarios")
    suspend fun salvar(@Body usuario: UsuarioRequest): Response<UsuarioResponse>

}