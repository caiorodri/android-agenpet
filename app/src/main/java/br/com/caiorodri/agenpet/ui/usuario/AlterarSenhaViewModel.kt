package br.com.caiorodri.agenpet.ui.usuario;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.model.usuario.UsuarioAlterarSenha;
import kotlinx.coroutines.launch;

class AlterarSenhaViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioController = UsuarioController(application);
    private val TAG = "AlterarSenhaVM";

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _erro = MutableLiveData<String?>(null);
    val erro: LiveData<String?> = _erro;

    private val _sucesso = MutableLiveData<Boolean>(false);
    val sucesso: LiveData<Boolean> = _sucesso;

    fun alterarSenha(senhaAntiga: String, senhaNova: String) {

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;
            _sucesso.value = false;

            try {

                val request = UsuarioAlterarSenha(senhaAntiga, senhaNova);

                val resultadoSucesso = usuarioController.alterarSenha(request);

                if (resultadoSucesso) {

                    Log.i(TAG, "Senha alterada com sucesso.");
                    _sucesso.postValue(true);

                } else {

                    Log.d(TAG, "Falha ao alterar senha.");
                    _erro.postValue(getApplication<Application>().getString(R.string.erro_alterar_senha_falha));

                }

            } catch (e: Exception) {

                Log.e(TAG, "Erro inesperado no viewModelScope ao alterar senha", e);
                _erro.postValue(getApplication<Application>().getString(R.string.erro_desconhecido));

            } finally {
                _isLoading.postValue(false);
            }
        };
    }

    fun resetarErro() {
        _erro.value = null;
    }

    fun resetarSucesso() {
        _sucesso.value = false;
    }
}