package br.com.caiorodri.agenpet.ui.agendamento;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.controller.AgendamentoController;
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest;
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse;
import br.com.caiorodri.agenpet.model.agendamento.Status;
import br.com.caiorodri.agenpet.model.agendamento.Tipo;
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import kotlinx.coroutines.launch;
import java.io.IOException;

class AgendamentoCadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val agendamentoController = AgendamentoController(application);
    private val usuarioController = UsuarioController(application);
    private val _tipos = MutableLiveData<List<Tipo>>();
    val tipos: LiveData<List<Tipo>> = _tipos;

    private val _status = MutableLiveData<List<Status>>();
    val status: LiveData<List<Status>> = _status;

    private val _veterinarios = MutableLiveData<List<UsuarioResponse>>();
    val veterinarios: LiveData<List<UsuarioResponse>> = _veterinarios;

    private val _agendamentoSalvo = MutableLiveData<AgendamentoResponse?>(null);
    val agendamentoSalvo: LiveData<AgendamentoResponse?> = _agendamentoSalvo;

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _recepcionistaAutoAtendimento = MutableLiveData<UsuarioResponse?>();
    val recepcionistaAutoAtendimento: LiveData<UsuarioResponse?> = _recepcionistaAutoAtendimento;

    private val _error = MutableLiveData<String?>(null);
    val error: LiveData<String?> = _error;

    init {
        carregarDadosIniciais();
    }

    private fun carregarDadosIniciais() {
        viewModelScope.launch {
            _isLoading.value = true;
            _error.value = null;
            try {

                val tiposResult = agendamentoController.listarTipos();
                _tipos.postValue(tiposResult);

                val statusResult = agendamentoController.listarStatus();
                _status.postValue(statusResult);

                val vets = usuarioController.listarVeterinarios();
                _veterinarios.postValue(vets);

                val recepcionista = usuarioController.recuperarRecepcionistaAutoAtendimento();
                _recepcionistaAutoAtendimento.postValue(recepcionista);

                if (recepcionista == null) {
                    Log.e("AgendamentoCadastroVM", "CRÍTICO: Recepcionista 'AUTO ATENDIMENTO' não encontrado.");
                    _error.postValue(getApplication<Application>().getString(R.string.erro_recepcionista_nao_encontrado));
                }

            } catch (e: Exception) {
                Log.e("AgendamentoCadastroVM", "Erro ao carregar dados iniciais", e);
                _error.postValue(getApplication<Application>().getString(R.string.erro_carregar_dados_agendamento));
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun salvarOuAtualizarAgendamento(request: AgendamentoRequest) {
        viewModelScope.launch {
            _isLoading.value = true;
            _error.value = null;
            _agendamentoSalvo.value = null;

            try {
                if (request.id == null) {
                    Log.d("AgendamentoCadastroVM", "Salvando novo agendamento...");
                    val agendamentoSalvo = agendamentoController.salvarAgendamento(request);
                    if (agendamentoSalvo != null) {
                        _agendamentoSalvo.postValue(agendamentoSalvo);
                    } else {
                        throw IOException(getApplication<Application>().getString(R.string.erro_agendamento_salvo_nulo));
                    }
                } else {
                    Log.d("AgendamentoCadastroVM", "Atualizando agendamento ID: ${request.id}");
                    val agendamentoAtualizado = agendamentoController.atualizarAgendamento(request);
                    if (agendamentoAtualizado != null) {
                        _agendamentoSalvo.postValue(agendamentoAtualizado);
                    } else {
                        throw IOException(getApplication<Application>().getString(R.string.erro_agendamento_atualizado_nulo));
                    }
                }

            } catch (e: IOException) {
                Log.e("AgendamentoCadastroVM", "Erro de rede", e);
                _error.postValue(getApplication<Application>().getString(R.string.erro_salvar_agendamento_rede));
            } catch (e: Exception) {
                Log.e("AgendamentoCadastroVM", "Erro ao salvar/atualizar", e);
                _error.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_salvar_agendamento_generico));
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun resetAgendamentoSalvo() {
        _agendamentoSalvo.value = null;
    }

    fun resetError() {
        _error.value = null;
    }
}