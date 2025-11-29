package br.com.caiorodri.agenpet.ui.usuario;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.model.usuario.Usuario;
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest;
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest;
import kotlinx.coroutines.launch;

class FuncionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioController = UsuarioController(application);

    private var listaCompleta: List<Usuario> = emptyList();

    private val _funcionarios = MutableLiveData<List<Usuario>>();
    val funcionarios: LiveData<List<Usuario>> = _funcionarios;

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    private val _sucessoCadastro = MutableLiveData<Boolean>(false);
    val sucessoCadastro: LiveData<Boolean> = _sucessoCadastro;

    fun listarFuncionarios() {

        viewModelScope.launch {

            _isLoading.value = true;

            try {
                val listaResponse = usuarioController.listarFuncionariosTodos();
                val listaConvertida = listaResponse.map { Usuario(it) };

                listaCompleta = listaConvertida;
                _funcionarios.postValue(listaConvertida);

            } catch (e: Exception) {

                _erro.postValue(e.message);

            } finally {

                _isLoading.value = false;

            }
        }
    }

    fun filtrarFuncionarios(query: String?) {
        if (query.isNullOrBlank()) {
            _funcionarios.value = listaCompleta;
        } else {
            val texto = query.lowercase().trim();

            val listaFiltrada = listaCompleta.filter { func ->
                func.nome.lowercase().contains(texto) ||
                        func.email.lowercase().contains(texto) ||
                        (func.perfil?.nome?.lowercase()?.contains(texto) == true)
            };

            _funcionarios.value = listaFiltrada;
        }
    }

    fun salvarFuncionario(usuario: UsuarioRequest) {

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;
            _sucessoCadastro.value = false;

            try {

                val resposta = usuarioController.salvar(usuario);

                if (resposta != null) {
                    _sucessoCadastro.postValue(true);
                } else {
                    _erro.postValue("Erro ao salvar funcionário.");
                }
            } catch (e: Exception) {
                _erro.postValue(e.message);
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun atualizarFuncionario(usuario: UsuarioUpdateRequest) {

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;
            _sucessoCadastro.value = false;

            try {
                val resposta = usuarioController.atualizar(usuario);

                if (resposta != null) {
                    _sucessoCadastro.postValue(true);
                } else {
                    _erro.postValue(getApplication<Application>().getString(R.string.erro_atualizar_funcionario));
                }
            } catch (e: Exception) {
                _erro.postValue(e.message);
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun resetSucesso() {
        _sucessoCadastro.value = false;
    }
}