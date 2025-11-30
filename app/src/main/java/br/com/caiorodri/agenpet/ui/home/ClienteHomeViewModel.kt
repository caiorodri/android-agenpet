package br.com.caiorodri.agenpet.ui.home;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.api.controller.AgendamentoController;
import br.com.caiorodri.agenpet.model.agendamento.Agendamento;
import br.com.caiorodri.agenpet.model.enums.StatusAgendamentoEnum
import kotlinx.coroutines.launch;

class ClienteHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val agendamentoController = AgendamentoController(application);

    private val _agendamentosRecentes = MutableLiveData<List<Agendamento>>();
    val agendamentosRecentes: LiveData<List<Agendamento>> = _agendamentosRecentes;

    private val _proximoAgendamento = MutableLiveData<Agendamento?>();
    val proximoAgendamento: LiveData<Agendamento?> = _proximoAgendamento;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun setAgendamentosIniciais(agendamentos: List<Agendamento>) {
        processarListaAgendamentos(agendamentos);
    }

    fun carregarAgendamentos(idUsuario: Long) {
        viewModelScope.launch {
            try {
                val agendamentosResponse = agendamentoController.listarAgendamentosByUsuarioId(idUsuario, 0, 25);
                val novosAgendamentos = agendamentosResponse.map { response -> Agendamento(response) };

                processarListaAgendamentos(novosAgendamentos);

            } catch (e: Exception) {
                _erro.postValue("Falha ao buscar agendamentos.");
            }
        }
    }

    private fun processarListaAgendamentos(listaCompleta: List<Agendamento>) {
        val agora = System.currentTimeMillis();

        val agendamentosFuturos = listaCompleta.filter { it.dataAgendamentoInicio > agora && it.status.id == StatusAgendamentoEnum.ABERTO.id };

        val agendamentosPassados = listaCompleta.filter { it.dataAgendamentoInicio <= agora };

        _proximoAgendamento.postValue(agendamentosFuturos.lastOrNull());

        _agendamentosRecentes.postValue(agendamentosPassados.take(5));
    }
}