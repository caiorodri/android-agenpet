package br.com.caiorodri.agenpet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.caiorodri.agenpet.model.usuario.Usuario

class HomeSharedViewModel : ViewModel() {

    private val _usuarioLogado = MutableLiveData<Usuario>();
    val usuarioLogado: LiveData<Usuario> = _usuarioLogado;

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    fun setUsuario(usuario: Usuario){

        _usuarioLogado.value = usuario;

    }

    fun setLoading(loading: Boolean){

        _isLoading.value = loading;

    }

}