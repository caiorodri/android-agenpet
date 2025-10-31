package br.com.caiorodri.agenpet.model.agendamento

import java.util.Date

data class AgendamentoRequest(
    val id: Long?,
    val idAnimal: Long,
    val idCliente: Long,
    val idVeterinario: Long,
    val idTipo: Int,
    val idStatus: Int,
    val dataAgendamentoInicio: Date,
    val dataAgendamentoFinal: Date,
    val descricao: String
)