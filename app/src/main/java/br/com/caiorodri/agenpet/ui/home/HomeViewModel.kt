package br.com.caiorodri.agenpet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.api.controller.AgendamentoController
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val agendamentoController = AgendamentoController();

    private val _agendamentos = MutableLiveData<List<Agendamento>>();
    val agendamentos: LiveData<List<Agendamento>> = _agendamentos;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun setAgendamentosIniciais(agendamentos: List<Agendamento>) {
        _agendamentos.value = agendamentos
    }

    fun carregarAgendamentos(idUsuario: Long) {

        viewModelScope.launch {

            try {

                val agendamentosResponse = agendamentoController.listarAgendamentosByUsuarioId(idUsuario);
                val novosAgendamentos = agendamentosResponse.map { response -> Agendamento(response) }

                _agendamentos.postValue(novosAgendamentos.take(3));

            } catch (e: Exception) {
                _erro.postValue("Falha ao buscar agendamentos.");
            }

        }

    }

}