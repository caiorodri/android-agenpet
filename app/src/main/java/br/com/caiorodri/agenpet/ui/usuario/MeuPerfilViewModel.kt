package br.com.caiorodri.agenpet.ui.usuario

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.api.controller.UsuarioController
import br.com.caiorodri.agenpet.model.usuario.Estado
import br.com.caiorodri.agenpet.model.usuario.LoginResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MeuPerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioController = UsuarioController(application);

    private val _usuario = MutableLiveData<UsuarioResponse?>();
    val usuario: LiveData<UsuarioResponse?> = _usuario;

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _error = MutableLiveData<String?>();
    val error: LiveData<String?> = _error;

    private val _updateSuccess = MutableLiveData<LoginResponse?>();
    val updateSuccess: LiveData<LoginResponse?> = _updateSuccess;

    fun setIsLoading(loading: Boolean) {
        _isLoading.value = loading;
    }

    fun salvarAlteracoes(usuarioRequest: UsuarioUpdateRequest) {

        viewModelScope.launch {

            _isLoading.value = true;
            _error.value = null;
            _updateSuccess.value = null;

            try {

                val usuarioAtualizado = usuarioController.atualizar(usuarioRequest);

                if (usuarioAtualizado != null) {

                    _updateSuccess.postValue(usuarioAtualizado);

                } else {

                    _error.postValue(getApplication<Application>().getString(R.string.erro_salvar_alteracoes));

                }

            } catch (e: IOException) {

                Log.e("MeuPerfilVM", "Erro de rede ao salvar perfil", e);
                _error.postValue(getApplication<Application>().getString(R.string.erro_conexao_geral));

            } catch (e: Exception) {

                Log.e("MeuPerfilVM", "Erro ao salvar perfil", e);
                _error.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_desconhecido));

            } finally {

                _isLoading.value = false;

            }

        }

    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = null;
    }

    fun resetError() {
        _error.value = null;
    }
}