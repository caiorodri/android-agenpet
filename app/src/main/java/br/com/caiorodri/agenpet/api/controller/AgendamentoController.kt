package br.com.caiorodri.agenpet.api.controller

import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse

class AgendamentoController {

    private val agendamentoService = ApiClient.agendamentoService

    suspend fun listarAgendamentosByUsuarioId(idUsuario: Long): List<AgendamentoResponse> {

        Log.i("Api", "[Inicio] - listarAgendamentosByUsuarioId");

        val response = agendamentoService.listarAgendamentosByUsuarioId(idUsuario = idUsuario);

        if (response.isSuccessful) {

            val agendamentos = response.body()?.content!!;

            Log.i("Api", "[Sucesso] - Agendamentos encontrados: ${agendamentos.size}");

            Log.i("Api", "[Fim] - listarAgendamentosByUsuarioId");

            return agendamentos;

        }

        Log.e("Api", "[Erro] - listarAgendamentosByUsuarioId");

        Log.i("Api", "[Fim] - listarAgendamentosByUsuarioId");

        return emptyList();

    }


}