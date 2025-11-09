package br.com.caiorodri.agenpet.ui.animal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.api.controller.AnimalController
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import kotlinx.coroutines.launch

class AnimalViewModel(application: Application) : AndroidViewModel(application) {

    private val animalController = AnimalController(application);
    private var listaCompleta: List<Animal> = emptyList()

    private val _animais = MutableLiveData<List<Animal>>()
    val animais: LiveData<List<Animal>> = _animais

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro

    fun setAnimaisIniciais(animais: List<Animal>){

        _animais.value = animais;

    }

    fun carregarAnimais(idDono: Long) {
        viewModelScope.launch {
            _isLoading.postValue(true);
            try {
                val animaisResponse = animalController.listarAnimaisByDonoId(idDono)
                val novosAnimais = animaisResponse.map { response -> Animal(response) }
                listaCompleta = novosAnimais
                _animais.postValue(novosAnimais)
            } catch (e: Exception) {
                _erro.postValue("Falha ao buscar os animais.")
            } finally {
                _isLoading.postValue(false);
            }
        }
    }

    fun filtrarAnimais(query: String?) {
        if (query.isNullOrBlank()) {
            _animais.value = listaCompleta
        } else {
            val queryLowerCase = query.lowercase().trim()
            val listaFiltrada = listaCompleta.filter { animal ->
                animal.nome.lowercase().contains(queryLowerCase) ||
                        animal.raca?.getNomeTraduzido(getApplication())?.lowercase()?.contains(queryLowerCase) == true ||
                        animal.raca?.especie?.getNomeTraduzido(getApplication())?.lowercase()?.contains(queryLowerCase) == true ||
                        animal.sexo?.getNomeTraduzido(getApplication())?.lowercase()?.contains(queryLowerCase) == true
            }
            _animais.value = listaFiltrada
        }
    }
}