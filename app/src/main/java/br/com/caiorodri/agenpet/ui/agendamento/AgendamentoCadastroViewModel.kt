package br.com.caiorodri.agenpet.ui.agendamento;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.repository.AgendamentoRepository
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest;
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse;
import br.com.caiorodri.agenpet.model.agendamento.Status;
import br.com.caiorodri.agenpet.model.agendamento.Tipo;
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import kotlinx.coroutines.launch;
import java.io.IOException;

class AgendamentoCadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val agendamentoRepository = AgendamentoRepository.getInstance(application);
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

    private val _horariosDisponiveis = MutableLiveData<List<String>>();
    val horariosDisponiveis: LiveData<List<String>> = _horariosDisponiveis;

    private val _isLoadingHorarios = MutableLiveData<Boolean>(false);
    val isLoadingHorarios: LiveData<Boolean> = _isLoadingHorarios;

    val tipoSelecionadoId = MutableLiveData<Int?>(null);
    val veterinarioSelecionadoId = MutableLiveData<Long?>(null);
    val dataSelecionadaApi = MutableLiveData<String?>(null);
    val dataSelecionadaTimestamp = MutableLiveData<Long?>(null);
    val horaSelecionada = MutableLiveData<String?>(null);

    private val _clientes = MutableLiveData<List<UsuarioResponse>>()
    val clientes: LiveData<List<UsuarioResponse>> = _clientes

    private var todosAnimais: List<Animal> = emptyList()

    private val _animaisFiltrados = MutableLiveData<List<Animal>>()
    val animaisFiltrados: LiveData<List<Animal>> = _animaisFiltrados

    fun carregarDadosIniciais(isRecepcionista: Boolean) {

        viewModelScope.launch {

            _isLoading.value = true;
            _error.value = null;

            try {

                val data = agendamentoRepository.getAgendamentoData(isRecepcionista);

                _tipos.postValue(data.tipos);
                _status.postValue(data.status);
                _veterinarios.postValue(data.veterinarios);

                if (isRecepcionista) {

                    _clientes.postValue(data.clientes ?: emptyList());
                    todosAnimais = data.animais?.map { Animal(it) } ?: emptyList();
                    _animaisFiltrados.postValue(emptyList());

                } else {

                    _recepcionistaAutoAtendimento.postValue(data.recepcionista);

                    if (data.recepcionista == null) {

                        Log.e("AgendamentoCadastroVM", "CRÍTICO: Recepcionista 'AUTO ATENDIMENTO' não encontrado.");
                        _error.postValue(getApplication<Application>().getString(R.string.erro_recepcionista_nao_encontrado));

                    }
                }



            } catch (e: Exception) {
                Log.e("AgendamentoCadastroVM", "Erro ao carregar dados iniciais", e);
                _error.postValue(getApplication<Application>().getString(R.string.erro_carregar_dados_agendamento));
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun buscarHorariosDisponiveis(idVeterinario: Long, data: String, idTipo: Int) {

        viewModelScope.launch {

            _isLoadingHorarios.value = true;

            try {

                val horarios = agendamentoRepository.listarHorariosDisponiveis(idVeterinario, data, idTipo);
                _horariosDisponiveis.postValue(horarios);

            } catch (e: Exception) {
                Log.e("AgendamentoCadastroVM", "Erro ao buscar horários disponíveis", e);
                _error.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_desconhecido));
            } finally {
                _isLoadingHorarios.value = false;
            }
        }
    }

    fun setTipoSelecionado(tipo: Tipo?) {
        tipoSelecionadoId.value = tipo?.id;
        veterinarioSelecionadoId.value = null;
        dataSelecionadaApi.value = null;
        dataSelecionadaTimestamp.value = null;
        horaSelecionada.value = null;
    }

    fun setVeterinarioSelecionado(vet: UsuarioResponse?) {
        veterinarioSelecionadoId.value = vet?.id;
        dataSelecionadaApi.value = null;
        dataSelecionadaTimestamp.value = null;
        horaSelecionada.value = null;
    }

    fun setDataSelecionada(timestamp: Long, dataApi: String) {
        dataSelecionadaTimestamp.value = timestamp;
        dataSelecionadaApi.value = dataApi;
        horaSelecionada.value = null;
    }

    fun setHoraSelecionada(hora: String?) {
        horaSelecionada.value = hora;
    }

    fun salvarOuAtualizarAgendamento(request: AgendamentoRequest) {

        viewModelScope.launch {

            _isLoading.value = true;
            _error.value = null;
            _agendamentoSalvo.value = null;

            try {

                if (request.id == null) {

                    Log.d("AgendamentoCadastroVM", "Salvando novo agendamento...");
                    val agendamentoSalvo = agendamentoRepository.salvarAgendamento(request);
                    _agendamentoSalvo.postValue(agendamentoSalvo);

                } else {

                    Log.d("AgendamentoCadastroVM", "Atualizando agendamento ID: ${request.id}");
                    val agendamentoAtualizado = agendamentoRepository.atualizarAgendamento(request);
                    _agendamentoSalvo.postValue(agendamentoAtualizado);

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

    fun filtrarAnimaisPorCliente(idCliente: Long) {

        val animaisDoCliente = todosAnimais.filter { it.dono.id == idCliente }
        _animaisFiltrados.value = animaisDoCliente

    }

    fun resetAgendamentoSalvo() {
        _agendamentoSalvo.value = null;
    }

    fun resetError() {
        _error.value = null;
    }
}