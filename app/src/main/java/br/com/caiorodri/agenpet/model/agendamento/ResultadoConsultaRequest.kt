package br.com.caiorodri.agenpet.model.agendamento

import android.os.Parcelable
import br.com.caiorodri.agenpet.ui.agendamento.AgendamentoCadastroFragment
import kotlinx.parcelize.Parcelize
import java.util.Date

data class ResultadoConsultaRequest(
    val id: Long?,
    val agendamento: AgendamentoCadastroComplementar?,
    val diagnosticoPrincipal: String,
    val observacoesVeterinario: String?,
    val prescricoes: List<ItemPrescricao>?,
    val dataRealizacao: Date?
)