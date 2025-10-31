package br.com.caiorodri.agenpet.model.usuario

data class UsuarioAlterarSenha(
    val senhaAntiga: String,
    val senhaNova: String
)
