package br.com.caiorodri.agenpet.ui.agendamento

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.api.controller.AgendamentoController
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AgendamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val agendamentoController = AgendamentoController(application);
    private var listaCompleta: List<Agendamento> = emptyList();

    private val _agendamentos = MutableLiveData<List<Agendamento>>();
    val agendamentos: LiveData<List<Agendamento>> = _agendamentos;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    private val _isLoading = MutableLiveData<Boolean>();
    val isLoading: LiveData<Boolean> = _isLoading;

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun setAgendamentosIniciais(agendamentos: List<Agendamento>) {
        _agendamentos.value = agendamentos
    }

    fun carregarAgendamentos(idUsuario: Long) {
        viewModelScope.launch {
            _isLoading.postValue(true);
            try {

                val agendamentosResponse = agendamentoController.listarAgendamentosByUsuarioId(idUsuario, 0, 25);
                val novosAgendamentos = agendamentosResponse.map { response -> Agendamento(response) }
                listaCompleta = novosAgendamentos;
                _agendamentos.postValue(novosAgendamentos);

            } catch (e: Exception) {

                _erro.postValue("Falha ao buscar agendamentos.")

            } finally {

                _isLoading.postValue(false);

            }
        }
    }

    fun filtrarAgendamentos(query: String?) {
        if (query.isNullOrBlank()) {
            _agendamentos.value = listaCompleta
        } else {

            val queryLowerCase = query.lowercase().trim();
            val listaFiltrada = listaCompleta.filter { agendamento ->

                val timestamp = agendamento.dataAgendamentoInicio
                val instant = Instant.ofEpochMilli(timestamp)
                val dataHoraLocal = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val dataFormatada = dataHoraLocal.format(dateFormatter)

                agendamento.animal.nome.lowercase().contains(queryLowerCase) ||
                        agendamento.veterinario.nome.lowercase().contains(queryLowerCase) ||
                        agendamento.tipo.getNomeTraduzido(getApplication()).lowercase().contains(queryLowerCase) ||
                        agendamento.status.getNomeTraduzido(getApplication()).lowercase().contains(queryLowerCase) ||
                        dataFormatada.contains(queryLowerCase) ||
                        dataHoraLocal.toString().contains(queryLowerCase)
            }
            _agendamentos.value = listaFiltrada
        }
    }
}