package br.com.caiorodri.agenpet.model.usuario

import android.os.Parcelable
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.animal.Animal
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val id: Long?,
    val nome: String,
    val email: String,
    val cpf: String?,
    val telefones: List<String>?,
    val dataNascimento: Long?,
    val emailRealizarConsultaRecebido: Boolean?,
    val endereco: Endereco?,
    val codigoRecuperacao: String?,
    val expiracaoCodigo: Long?,
    val perfil: Perfil?,
    val status: Status?,
    val senha: String?,
    var agendamentos: List<Agendamento>?,
    var animais: List<Animal>?

    ): Parcelable {

    constructor(usuarioResponse: UsuarioResponse): this (
        usuarioResponse.id,
        usuarioResponse.nome,
        usuarioResponse.email,
        usuarioResponse.cpf,
        usuarioResponse.telefones,
        usuarioResponse.dataNascimento?.time,
        usuarioResponse.emailRealizarConsultaRecebido,
        usuarioResponse.endereco,
        usuarioResponse.codigoRecuperacao,
        usuarioResponse.expiracaoCodigo?.time,
        usuarioResponse.perfil,
        usuarioResponse.status,
        usuarioResponse.senha,
        listOf(),
        listOf()
        )

}