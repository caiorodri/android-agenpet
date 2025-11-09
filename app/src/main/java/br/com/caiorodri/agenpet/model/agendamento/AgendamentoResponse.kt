package br.com.caiorodri.agenpet.model.agendamento

import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import java.util.Date

data class AgendamentoResponse(
    val id: Long,
    val animal: AnimalResponse,
    val cliente: UsuarioResponse,
    val dataAgendamentoFinal: Date,
    val dataAgendamentoInicio: Date,
    val dataCriacao: Date,
    val descricao: String,
    val recepcionista: UsuarioResponse,
    val status: Status,
    val tipo: Tipo,
    val veterinario: UsuarioResponse
)