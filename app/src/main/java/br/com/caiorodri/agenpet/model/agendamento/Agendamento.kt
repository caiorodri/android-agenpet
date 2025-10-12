package br.com.caiorodri.agenpet.model.agendamento

import android.os.Parcelable
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.agendamento.Tipo
import br.com.caiorodri.agenpet.model.usuario.Usuario
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Agendamento(
    val id: Long,
    val animal: Animal,
    val cliente: Usuario,
    val dataAgendamentoFinal: Long,
    val dataAgendamentoInicio: Long,
    val dataCriacao: Long,
    val descricao: String,
    val recepcionista: Usuario,
    val status: Status,
    val tipo: Tipo,
    val veterinario: Usuario
) : Parcelable {

    constructor(agendamentoResponse: AgendamentoResponse) : this (
        agendamentoResponse.id,
        Animal(agendamentoResponse.animal),
        Usuario(agendamentoResponse.cliente),
        agendamentoResponse.dataAgendamentoFinal.time,
        agendamentoResponse.dataAgendamentoInicio.time,
        agendamentoResponse.dataCriacao.time,
        agendamentoResponse.descricao,
        Usuario(agendamentoResponse.recepcionista),
        agendamentoResponse.status,
        agendamentoResponse.tipo,
        Usuario(agendamentoResponse.veterinario)

    )

}