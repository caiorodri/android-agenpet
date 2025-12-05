package br.com.caiorodri.agenpet.model.agendamento

import java.util.Date

data class ResultadoConsultaResponse(
    val id: Long?,
    val agendamento: AgendamentoResponse?,
    val diagnosticoPrincipal: String,
    val observacoesVeterinario: String?,
    val prescricoes: List<ItemPrescricao>?,
    val dataRealizacao: Date
)