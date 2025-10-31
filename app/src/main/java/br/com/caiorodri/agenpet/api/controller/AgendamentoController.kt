package br.com.caiorodri.agenpet.api.controller

import android.content.Context
import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse
import br.com.caiorodri.agenpet.model.agendamento.Status
import br.com.caiorodri.agenpet.model.agendamento.Tipo
import java.io.IOException

class AgendamentoController(private val context: Context) {

    private val agendamentoService = ApiClient.getAgendamentoService(context)
    private val TAG = "AgendamentoController"

    suspend fun listarAgendamentosByUsuarioId(
        idUsuario: Long,
        pagina: Int,
        itens: Int
    ): List<AgendamentoResponse> {
        val endpoint = "listarAgendamentosByUsuarioId"
        Log.i(TAG, "[$endpoint] - Inicio (UsuarioID: $idUsuario)")

        try {
            val response = agendamentoService.listarAgendamentosByUsuarioId(
                idUsuario = idUsuario,
                pagina = pagina,
                quantidadeItens = itens
            )

            if (response.isSuccessful) {
                val agendamentos = response.body()?.content ?: emptyList()
                Log.i(TAG, "[$endpoint] - Sucesso. ${agendamentos.size} agendamentos encontrados.")
                Log.i(TAG, "[$endpoint] - Fim")
                return agendamentos
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e)
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)")
        return emptyList()
    }

    suspend fun recuperarAgendamento(id: Long): AgendamentoResponse? {
        val endpoint = "recuperarAgendamento"
        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)")

        try {
            val response = agendamentoService.recuperar(id)
            if (response.isSuccessful) {
                Log.i(TAG, "[$endpoint] - Sucesso. Agendamento encontrado.")
                Log.i(TAG, "[$endpoint] - Fim")
                return response.body()
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim")
        return null
    }

    suspend fun salvarAgendamento(request: AgendamentoRequest): AgendamentoResponse? {
        val endpoint = "salvarAgendamento"
        Log.i(TAG, "[$endpoint] - Inicio")

        try {
            val response = agendamentoService.salvar(request)
            if (response.isSuccessful) {
                Log.i(TAG, "[$endpoint] - Sucesso. Agendamento criado.")
                Log.i(TAG, "[$endpoint] - Fim")
                return response.body()
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim")
        return null
    }

    suspend fun atualizarAgendamento(request: AgendamentoRequest): AgendamentoResponse? {
        val endpoint = "atualizarAgendamento"
        Log.i(TAG, "[$endpoint] - Inicio (ID: ${request.id})")

        try {
            val response = agendamentoService.atualizar(request)
            if (response.isSuccessful) {
                Log.i(TAG, "[$endpoint] - Sucesso. Agendamento atualizado.")
                Log.i(TAG, "[$endpoint] - Fim")
                return response.body()
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim")
        return null
    }

    suspend fun deletarAgendamento(id: Long): Boolean {
        val endpoint = "deletarAgendamento"
        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)")

        try {
            val response = agendamentoService.deletar(id)
            if (response.isSuccessful) {
                Log.i(TAG, "[$endpoint] - Sucesso. Agendamento deletado.")
                Log.i(TAG, "[$endpoint] - Fim")
                return true
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando false)")
        return false
    }

    suspend fun listarStatus(): List<Status> {
        val endpoint = "listarStatus"
        Log.i(TAG, "[$endpoint] - Inicio")

        try {
            val response = agendamentoService.listarStatus()
            if (response.isSuccessful) {
                val statusList = response.body() ?: emptyList()
                Log.i(TAG, "[$endpoint] - Sucesso. ${statusList.size} status encontrados.")
                Log.i(TAG, "[$endpoint] - Fim")
                return statusList
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)")
        return emptyList()
    }

    suspend fun listarTipos(): List<Tipo> {

        val endpoint = "listarTipos"

        Log.i(TAG, "[$endpoint] - Inicio")

        try {
            val response = agendamentoService.listarTipos()
            if (response.isSuccessful) {
                val tipoList = response.body() ?: emptyList()
                Log.i(TAG, "[$endpoint] - Sucesso. ${tipoList.size} tipos encontrados.")
                Log.i(TAG, "[$endpoint] - Fim")
                return tipoList
            } else {
                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$endpoint] - Erro: ${e.message}", e)
        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)")
        return emptyList()
    }
}