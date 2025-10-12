package br.com.caiorodri.agenpet.model.animal

import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.animal.Sexo
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse

data class AnimalResponse(
    val altura: Double,
    val castrado: Boolean,
    val dataNascimento: String,
    val descricao: String,
    val dono: UsuarioResponse,
    val id: Long?,
    val nome: String,
    val peso: Double,
    val raca: Raca,
    val sexo: Sexo
)