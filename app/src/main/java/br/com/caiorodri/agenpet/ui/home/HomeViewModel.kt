package br.com.caiorodri.agenpet.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.api.controller.AgendamentoController
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.usuario.Usuario
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val agendamentoController = AgendamentoController(application);

    private val _agendamentosDia = MutableLiveData<List<Agendamento>>();
    val agendamentosDia: LiveData<List<Agendamento>> = _agendamentosDia;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun carregarDadosHome(usuario: Usuario){

        val perfil = usuario.perfil?.nome;

        if(perfil == "ADMINISTRADOR"){

            return;

        } else if (perfil == "RECEPCIONISTA"){

            carregarAgendamentosDia(usuario.id!!);

        } else {

            carregarAgendamentosVeterinarioDia(usuario.id!!);

        }

    }

    fun carregarAgendamentosDia(id: Long){

        viewModelScope.launch {

            try {

                val dataHoje = LocalDate.now().toString();
                val agendamentoResponse = agendamentoController.listarAgendamentosNaData(dataHoje);
                val agendamentos = agendamentoResponse.map { Agendamento(it) }

                _agendamentosDia.postValue(agendamentos);

            } catch (e: Exception){

                _erro.postValue(e.message);

            }

        }

    }

    fun carregarAgendamentosVeterinarioDia(id: Long){

        viewModelScope.launch {

            try {

                val dataHoje = LocalDate.now().toString()
                val agendamentoResponse = agendamentoController.listarAgendamentosVeterinarioNaData(id, dataHoje);
                val agendamentos = agendamentoResponse.map { Agendamento(it) }

                _agendamentosDia.postValue(agendamentos);

            } catch (e: Exception){

                _erro.postValue(e.message)

            }

        }

    }

}