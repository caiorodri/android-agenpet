package br.com.caiorodri.agenpet.model.usuario

import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import java.util.Date

data class UsuarioUpdateRequest(
    val id: Long?,
    val nome: String,
    val email: String,
    val telefones: List<String>,
    val dataNascimento: Date?,
    val endereco: Endereco?,
    val perfil: Perfil?,
    val status: Status?,
    val urlImagem: String?
)
