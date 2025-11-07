package br.com.caiorodri.agenpet.ui.animal;

import android.app.Application;
import android.net.Uri
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.repository.AnimalRepository;
import br.com.caiorodri.agenpet.model.animal.Animal;
import br.com.caiorodri.agenpet.model.animal.AnimalResponse;
import br.com.caiorodri.agenpet.model.animal.Especie;
import br.com.caiorodri.agenpet.model.animal.Raca;
import br.com.caiorodri.agenpet.model.animal.Sexo;
import kotlinx.coroutines.launch;
import java.io.IOException;

class AnimalCadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val animalRepository = AnimalRepository.getInstance(application);

    private val _especies = MutableLiveData<List<Especie>>();
    val especies: LiveData<List<Especie>> = _especies;

    var listaCompletaRacas: List<Raca> = emptyList(); private set;

    private val _racasFiltradas = MutableLiveData<List<Raca>>(emptyList());
    val racasFiltradas: LiveData<List<Raca>> = _racasFiltradas;

    private val _sexos = MutableLiveData<List<Sexo>>();
    val sexos: LiveData<List<Sexo>> = _sexos;

    private val _animalSalvoComSucesso = MutableLiveData<AnimalResponse?>(null);
    val animalSalvoComSucesso: LiveData<AnimalResponse?> = _animalSalvoComSucesso;

    private val _animalRemovidoComSucesso = MutableLiveData<Boolean?>(null);
    val animalRemovidoComSucesso: LiveData<Boolean?> = _animalRemovidoComSucesso;

    private val _isLoadingDadosIniciais = MutableLiveData<Boolean>();
    val isLoadingDadosIniciais: LiveData<Boolean> = _isLoadingDadosIniciais;

    private val _erroDadosIniciais = MutableLiveData<String?>();
    val erroDadosIniciais: LiveData<String?> = _erroDadosIniciais;

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _actionError = MutableLiveData<String?>(null);
    val actionError: LiveData<String?> = _actionError;
    val fotoUriSelecionada = MutableLiveData<Uri?>(null);
    val especieSelecionada = MutableLiveData<Especie?>(null);
    val racaSelecionada = MutableLiveData<Raca?>(null);

    init {
        carregarDadosIniciais();
        _sexos.value = listOf(Sexo(1, "Macho"), Sexo(2, "Fêmea"), Sexo(3, "Desconhecido"));
    }

    private fun carregarDadosIniciais() {

        viewModelScope.launch {

            _isLoadingDadosIniciais.value = true;
            _erroDadosIniciais.value = null;

            try {

                val data = animalRepository.getAnimalData();

                listaCompletaRacas = data.racas;
                _especies.postValue(data.especies);

            } catch (e: Exception) {

                Log.e("CadastroAnimalVM", "Erro ao carregar dados iniciais (espécies/raças)", e);

                _erroDadosIniciais.postValue(getApplication<Application>().getString(R.string.erro_carregar_especies_racas));
                _especies.postValue(emptyList());
                listaCompletaRacas = emptyList();
                _racasFiltradas.postValue(emptyList());

            } finally {
                _isLoadingDadosIniciais.value = false;
            }
        }
    }

    fun filtrarRacasPorEspecie(especieSelecionada: Especie?) {
        if (especieSelecionada == null) {
            _racasFiltradas.value = emptyList();
            return;
        }
        _racasFiltradas.value = listaCompletaRacas.filter { raca ->
            raca.especie?.id == especieSelecionada.id
        };
    }

    fun setIsLoading(loading: Boolean) {
        _isLoading.value = loading;
    }

    fun setEspecie(especie: Especie?) {
        especieSelecionada.value = especie;
        racaSelecionada.value = null;
        filtrarRacasPorEspecie(especie);
    }

    fun setRaca(raca: Raca?) {
        racaSelecionada.value = raca;
    }

    fun salvarAnimal(animal: Animal) {
        viewModelScope.launch {
            _actionError.value = null;
            _animalSalvoComSucesso.value = null;

            try {
                if (animal.id == null || animal.id == 0L) {
                    Log.d("CadastroAnimalVM", "Salvando novo animal: ${animal.nome}");
                    val animalSalvo = animalRepository.salvarAnimal(animal);
                    Log.d("CadastroAnimalVM", "Salvo com sucesso, ID: ${animalSalvo.id}");
                    _animalSalvoComSucesso.postValue(animalSalvo);
                } else {
                    Log.d("CadastroAnimalVM", "Atualizando animal ID ${animal.id}: ${animal.nome}");
                    val animalAtualizado = animalRepository.atualizarAnimal(animal);
                    Log.d("CadastroAnimalVM", "Atualizado com sucesso, ID: ${animalAtualizado.id}");
                    _animalSalvoComSucesso.postValue(animalAtualizado);
                }

            } catch (e: IOException) {
                Log.e("CadastroAnimalVM", "Erro de rede ao salvar animal", e);
                _actionError.postValue(getApplication<Application>().getString(R.string.erro_salvar_animal_rede));
            } catch (e: Exception) {
                Log.e("CadastroAnimalVM", "Erro ao salvar animal", e);
                _actionError.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_salvar_animal_generico));
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun removerAnimal(animalId: Long) {
        viewModelScope.launch {
            _isLoading.value = true;
            _actionError.value = null;
            _animalRemovidoComSucesso.value = null;

            try {
                Log.d("CadastroAnimalVM", "Removendo animal ID $animalId");
                animalRepository.removerAnimal(animalId);
                Log.d("CadastroAnimalVM", "Removido com sucesso");
                _animalRemovidoComSucesso.postValue(true);

            } catch (e: IOException) {
                Log.e("CadastroAnimalVM", "Erro de rede ao remover animal", e);
                _actionError.postValue(getApplication<Application>().getString(R.string.erro_remover_animal_rede));
            } catch (e: Exception) {
                Log.e("CadastroAnimalVM", "Erro ao remover animal", e);
                _actionError.postValue(e.message ?: getApplication<Application>().getString(R.string.erro_remover_animal_generico));
            } finally {
                _isLoading.value = false;
            }
        }
    }

    fun resetAnimalSalvo() {
        _animalSalvoComSucesso.value = null;
    }

    fun resetAnimalRemovido() {
        _animalRemovidoComSucesso.value = null;
    }

    fun resetActionError() {
        _actionError.value = null;
    }
}