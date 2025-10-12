package br.com.caiorodri.agenpet.ui.animal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.api.controller.AnimalController
import br.com.caiorodri.agenpet.model.animal.Animal
import kotlinx.coroutines.launch

class AnimalViewModel : ViewModel() {

    private val animalController = AnimalController()
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
                        animal.raca?.nome?.lowercase()?.contains(queryLowerCase) == true
            }
            _animais.value = listaFiltrada
        }
    }
}