package br.com.caiorodri.agenpet.model.agendamento

import br.com.caiorodri.agenpet.model.animal.AnimalCadastroComplementar
import br.com.caiorodri.agenpet.model.usuario.UsuarioCadastroComplementar
import java.util.Date

data class AgendamentoRequest(
    val id: Long?,
    val animal: AnimalCadastroComplementar,
    val cliente: UsuarioCadastroComplementar,
    val veterinario: UsuarioCadastroComplementar,
    val recepcionista: UsuarioCadastroComplementar,
    val tipo: Tipo,
    val status: Status,
    val dataAgendamentoInicio: Date,
    val dataAgendamentoFinal: Date,
    val dataCricao: Date = Date(),
    val descricao: String
)