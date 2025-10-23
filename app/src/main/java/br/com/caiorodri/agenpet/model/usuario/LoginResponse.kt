package br.com.caiorodri.agenpet.model.usuario

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)