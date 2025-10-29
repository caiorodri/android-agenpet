package br.com.caiorodri.agenpet.model.usuario

import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import java.util.Date

data class UsuarioUpdateSenhaRequest(
    val id: Long,
    val senha: String,
    val codigo: String,
)
