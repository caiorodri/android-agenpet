package br.com.caiorodri.agenpet.ui.agendamento

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.api.controller.AgendamentoController
import br.com.caiorodri.agenpet.api.controller.UsuarioController
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse
import br.com.caiorodri.agenpet.model.agendamento.Status
import br.com.caiorodri.agenpet.model.agendamento.Tipo
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import kotlinx.coroutines.launch
import java.io.IOException

class AgendamentoCadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val agendamentoController = AgendamentoController(application)
    private val usuarioController = UsuarioController(application)
    private val _tipos = MutableLiveData<List<Tipo>>()
    val tipos: LiveData<List<Tipo>> = _tipos

    private val _status = MutableLiveData<List<Status>>()
    val status: LiveData<List<Status>> = _status

    private val _veterinarios = MutableLiveData<List<UsuarioResponse>>()
    val veterinarios: LiveData<List<UsuarioResponse>> = _veterinarios

    private val _agendamentoSalvo = MutableLiveData<AgendamentoResponse?>(null)
    val agendamentoSalvo: LiveData<AgendamentoResponse?> = _agendamentoSalvo

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _recepcionistaAutoAtendimento = MutableLiveData<UsuarioResponse?>()
    val recepcionistaAutoAtendimento: LiveData<UsuarioResponse?> = _recepcionistaAutoAtendimento

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        carregarDadosIniciais()
    }

    private fun carregarDadosIniciais() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {

                val tiposResult = agendamentoController.listarTipos()
                _tipos.postValue(tiposResult)

                val statusResult = agendamentoController.listarStatus()
                _status.postValue(statusResult)

                val vets = usuarioController.listarVeterinarios()
                _veterinarios.postValue(vets)

                val recepcionista = usuarioController.recuperarRecepcionistaAutoAtendimento()
                _recepcionistaAutoAtendimento.postValue(recepcionista)

                if (recepcionista == null) {
                    Log.e("AgendamentoCadastroVM", "CRÍTICO: Recepcionista 'AUTO ATENDIMENTO' não encontrado.")
                    _error.postValue("Erro de configuração do sistema. Contate o suporte.")
                }

            } catch (e: Exception) {
                Log.e("AgendamentoCadastroVM", "Erro ao carregar dados iniciais", e)
                _error.postValue("Não foi possível carregar os dados necessários.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun salvarOuAtualizarAgendamento(request: AgendamentoRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _agendamentoSalvo.value = null

            try {
                if (request.id == null) {
                    Log.d("AgendamentoCadastroVM", "Salvando novo agendamento...")
                    val agendamentoSalvo = agendamentoController.salvarAgendamento(request)
                    if (agendamentoSalvo != null) {
                        _agendamentoSalvo.postValue(agendamentoSalvo)
                    } else {
                        throw IOException("Resposta nula do servidor ao salvar agendamento.")
                    }
                } else {
                    Log.d("AgendamentoCadastroVM", "Atualizando agendamento ID: ${request.id}")
                    val agendamentoAtualizado = agendamentoController.atualizarAgendamento(request)
                    if (agendamentoAtualizado != null) {
                        _agendamentoSalvo.postValue(agendamentoAtualizado)
                    } else {
                        throw IOException("Resposta nula do servidor ao atualizar agendamento.")
                    }
                }

            } catch (e: IOException) {
                Log.e("AgendamentoCadastroVM", "Erro de rede", e)
                _error.postValue("Erro de conexão. Verifique sua internet.")
            } catch (e: Exception) {
                Log.e("AgendamentoCadastroVM", "Erro ao salvar/atualizar", e)
                _error.postValue(e.message ?: "Ocorreu um erro desconhecido.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAgendamentoSalvo() {
        _agendamentoSalvo.value = null
    }

    fun resetError() {
        _error.value = null
    }
}