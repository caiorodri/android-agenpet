package br.com.caiorodri.agenpet.ui.animal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.model.animal.Raca
import br.com.caiorodri.agenpet.model.animal.Sexo
import kotlinx.coroutines.launch

class AnimalCadastroViewModel : ViewModel() {

    private val _especies = MutableLiveData<List<Especie>>()
    val especies: LiveData<List<Especie>> = _especies;
    private var listaCompletaRacas: List<Raca> = emptyList()
    private val _racasFiltradas = MutableLiveData<List<Raca>>()
    val racasFiltradas: LiveData<List<Raca>> = _racasFiltradas

    private val _sexos = MutableLiveData<List<Sexo>>()
    val sexos: LiveData<List<Sexo>> = _sexos

    private val _eventoSalvo = MutableLiveData<Boolean>()
    val eventoSalvo: LiveData<Boolean> = _eventoSalvo

    init {
        carregarDadosIniciais()
    }

    private fun carregarDadosIniciais() {
        val especieCao = Especie(1, "Cão")
        val especieGato = Especie(2, "Gato")

        _especies.value = listOf(especieCao, especieGato)

        listaCompletaRacas = listOf(
            Raca(1,  "SRD (Vira-lata)", especieCao),

            Raca(2, "Golden Retriever", especieCao),
            Raca(3, "Poodle", especieCao),
            Raca(4, "Siamês", especieGato),
            Raca(5, "Persa", especieGato)
        )

        _sexos.value = listOf(Sexo(1, "Macho"), Sexo(2, "Fêmea"))
    }

    fun filtrarRacasPorEspecie(especieSelecionada: Especie?) {
        if (especieSelecionada == null) {
            _racasFiltradas.value = emptyList()
            return
        }
        _racasFiltradas.value = listaCompletaRacas.filter { raca ->
            raca.especie?.id == especieSelecionada.id
        }
    }

    fun salvarAnimal(animal: Animal) {
        viewModelScope.launch {
            if (animal.id == null || animal.id == 0L) {
                Log.d("CadastroAnimalVM", "Salvando novo animal: ${animal.nome}")
            } else {
                Log.d("CadastroAnimalVM", "Atualizando animal ID ${animal.id}: ${animal.nome}")
            }
            _eventoSalvo.postValue(true)
        }
    }
}