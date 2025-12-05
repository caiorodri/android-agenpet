package br.com.caiorodri.agenpet.model.agendamento

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultadoConsulta(
    val id: Long?,
    val agendamento: Agendamento?,
    val diagnosticoPrincipal: String,
    val observacoesVeterinario: String?,
    val prescricoes: List<ItemPrescricao>?,
    val dataRealizacao: Long?
) : Parcelable