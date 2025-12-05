package br.com.caiorodri.agenpet.ui.agendamento;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.api.controller.AgendamentoController;
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.ItemPrescricao
import br.com.caiorodri.agenpet.model.agendamento.ResultadoConsulta
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoCadastroComplementar
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.ResultadoConsultaRequest
import kotlinx.coroutines.launch;

class PosConsultaCadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val controller = AgendamentoController(application);

    private val _prescricoes = MutableLiveData<MutableList<ItemPrescricao>>(mutableListOf());
    val prescricoes: LiveData<MutableList<ItemPrescricao>> = _prescricoes;

    private val _isLoading = MutableLiveData<Boolean>();
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _sucesso = MutableLiveData<Boolean>();
    val sucesso: LiveData<Boolean> = _sucesso;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun adicionarPrescricao(item: ItemPrescricao) {

        val listaAtual = _prescricoes.value ?: mutableListOf();
        listaAtual.add(item);
        _prescricoes.value = listaAtual;

    }

    fun removerPrescricao(item: ItemPrescricao) {

        val listaAtual = _prescricoes.value ?: mutableListOf();
        listaAtual.remove(item);
        _prescricoes.value = listaAtual;

    }

    fun finalizarConsulta(
        agendamento: Agendamento,
        diagnostico: String,
        observacoes: String
    ) {

        if (diagnostico.isBlank()) {

            val msg = getApplication<Application>().getString(R.string.erro_diagnostico_obrigatorio);
            _erro.value = msg;
            return;
        }

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;

            val resultado = ResultadoConsultaRequest(
                id = null,
                agendamento = AgendamentoCadastroComplementar(agendamento.id),
                diagnosticoPrincipal = diagnostico,
                observacoesVeterinario = observacoes,
                prescricoes = _prescricoes.value ?: emptyList(),
                dataRealizacao = null
            );

            try {

                val salvo = controller.salvarResultadoConsulta(resultado);

                if (salvo != null) {
                    _sucesso.value = true;
                } else {
                    val msg = getApplication<Application>().getString(R.string.erro_salvar_resultado_conexao);
                    _erro.value = msg;
                }

            } catch (e: Exception) {

                _erro.value = "Erro: ${e.message}";

            } finally {

                _isLoading.value = false;

            }
        }
    }
}