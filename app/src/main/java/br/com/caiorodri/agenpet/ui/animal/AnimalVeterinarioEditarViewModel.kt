package br.com.caiorodri.agenpet.ui.animal;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.api.controller.AnimalController;
import br.com.caiorodri.agenpet.model.animal.Animal;
import kotlinx.coroutines.launch;

class AnimalVeterinarioEditarViewModel(application: Application) : AndroidViewModel(application) {

    private val animalController = AnimalController(application);

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _sucessoAtualizacao = MutableLiveData<Boolean>(false);
    val sucessoAtualizacao: LiveData<Boolean> = _sucessoAtualizacao;

    private val _animal = MutableLiveData<Animal?>();
    val animal: LiveData<Animal?> = _animal;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun recuperarAnimal(id: Long) {

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;

            try {

                val animalResponse = animalController.recuperarById(id);

                if (animalResponse != null) {

                    _animal.postValue(Animal(animalResponse));

                } else {

                    _erro.postValue("Animal não encontrado.");

                }

            } catch (e: Exception) {

                _erro.postValue(e.message);

            } finally {

                _isLoading.value = false;

            }

        }
    }

    fun atualizarDadosClinicos(animal: Animal) {

        viewModelScope.launch {

            _isLoading.value = true;
            _erro.value = null;
            _sucessoAtualizacao.value = false;

            try {



                val resultado = animalController.atualizarAnimal(animal);

                if (resultado != null) {

                    _sucessoAtualizacao.postValue(true);

                } else {

                    _erro.postValue("Erro ao atualizar dados clínicos.");

                }

            } catch (e: Exception) {

                _erro.postValue(e.message);

            } finally {

                _isLoading.value = false;

            }

        }

    }

    fun resetSucesso() {
        _sucessoAtualizacao.value = false;
    }
}