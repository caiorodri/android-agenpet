package br.com.caiorodri.agenpet.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.usuario.Usuario

class HomeSharedViewModel : ViewModel() {

    private val _usuarioLogado = MutableLiveData<Usuario>()
    val usuarioLogado: LiveData<Usuario> = _usuarioLogado

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun setUsuario(usuario: Usuario) {
        _usuarioLogado.value = usuario
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun atualizarAgendamentoLocalmente(agendamento: Agendamento) {

        val usuarioAtual = _usuarioLogado.value;
        if (usuarioAtual == null) {
            Log.w("HomeSharedVM", "Usuário nulo, não foi possível fazer a atualização.");
            return
        }

        val listaAgendamentosAtual = usuarioAtual.agendamentos?.toMutableList() ?: mutableListOf()

        val index = listaAgendamentosAtual.indexOfFirst { it.id == agendamento.id }

        if (index != -1) {
            listaAgendamentosAtual[index] = agendamento
            Log.d("HomeSharedVM", "Agendamengo ID ${agendamento.id} atualizado localmente.")
        } else {
            listaAgendamentosAtual.add(0, agendamento)
            Log.d("HomeSharedVM", "Novo Agendamento ID ${agendamento.id} adicionado localmente.")
        }

        val usuarioAtualizado = usuarioAtual.copy(agendamentos = listaAgendamentosAtual)

        _usuarioLogado.value = usuarioAtualizado
    }

    fun atualizarAnimalLocalmente(animal: Animal) {
        val usuarioAtual = _usuarioLogado.value
        if (usuarioAtual == null) {
            Log.w("HomeSharedVM", "Usuário nulo, não foi possível fazer a atualização otimista.")
            return
        }

        val listaAnimaisAtual = usuarioAtual.animais?.toMutableList() ?: mutableListOf()

        val index = listaAnimaisAtual.indexOfFirst { it.id == animal.id }

        if (index != -1) {
            listaAnimaisAtual[index] = animal
            Log.d("HomeSharedVM", "Animal ID ${animal.id} atualizado localmente.")
        } else {
            listaAnimaisAtual.add(0, animal)
            Log.d("HomeSharedVM", "Novo animal ID ${animal.id} adicionado localmente.")
        }

        val usuarioAtualizado = usuarioAtual.copy(animais = listaAnimaisAtual)

        _usuarioLogado.value = usuarioAtualizado
    }

    fun removerAnimalLocalmente(animalId: Long) {
        val usuarioAtual = _usuarioLogado.value
        if (usuarioAtual == null) {
            Log.w("HomeSharedVM", "Usuário nulo, não foi possível remover localmente.")
            return
        }

        val listaAnimaisAtual = usuarioAtual.animais?.toMutableList() ?: mutableListOf()

        val removido = listaAnimaisAtual.removeAll { it.id == animalId }

        if (removido) {
            Log.d("HomeSharedVM", "Animal ID $animalId removido localmente.")
            val usuarioAtualizado = usuarioAtual.copy(animais = listaAnimaisAtual)
            _usuarioLogado.value = usuarioAtualizado
        } else {
            Log.w("HomeSharedVM", "Animal ID $animalId não encontrado para remoção local.")
        }
    }
}