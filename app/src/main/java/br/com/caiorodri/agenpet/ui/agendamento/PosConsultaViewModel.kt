package br.com.caiorodri.agenpet.ui.agendamento;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.api.controller.AgendamentoController;
import br.com.caiorodri.agenpet.model.agendamento.ResultadoConsulta
import br.com.caiorodri.agenpet.model.agendamento.ResultadoConsultaResponse
import kotlinx.coroutines.launch;

class PosConsultaViewModel(application: Application) : AndroidViewModel(application) {

    private val controller = AgendamentoController(application);

    private val _resultadoConsulta = MutableLiveData<ResultadoConsultaResponse?>();
    val resultadoConsulta: LiveData<ResultadoConsultaResponse?> = _resultadoConsulta;

    private val _isLoading = MutableLiveData<Boolean>();
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun carregarResultadoConsulta(idAgendamento: Long) {

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;

            try {

                val resultado = controller.recuperarResultadoConsulta(idAgendamento);

                if (resultado != null) {
                    _resultadoConsulta.value = resultado;
                } else {
                    _resultadoConsulta.value = null;
                    _erro.value = "Nenhum resultado encontrado para este agendamento.";
                }

            } catch (e: Exception) {

                _erro.value = "Erro ao buscar dados: ${e.message}";

            } finally {

                _isLoading.value = false;

            }
        }
    }
}