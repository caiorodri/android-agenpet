package br.com.caiorodri.agenpet.api.repository

import android.content.Context
import android.util.Log
import br.com.caiorodri.agenpet.api.controller.AnimalController
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.model.animal.Raca
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class AnimalData(
    val especies: List<Especie>,
    val racas: List<Raca>
)

class AnimalRepository private constructor(context: Context) {

    private val animalController = AnimalController(context.applicationContext)

    private var especiesCache: List<Especie>? = null
    private var racasCache: List<Raca>? = null
    private val mutex = Mutex()

    suspend fun getAnimalData(): AnimalData {
        mutex.withLock {
            val especies = especiesCache
            val racas = racasCache

            if (especies != null && racas != null) {
                Log.i("AnimalRepository", "Retornando dados do cache.")
                return AnimalData(especies, racas)
            }

            Log.i("AnimalRepository", "Buscando dados da rede.")
            try {
                val (fetchedEspecies, fetchedRacas) = coroutineScope {
                    val especiesJob = async { animalController.listarEspecies() }
                    val racasJob = async { animalController.listarRacas() }
                    especiesJob.await() to racasJob.await()
                }

                especiesCache = fetchedEspecies
                racasCache = fetchedRacas
                return AnimalData(fetchedEspecies, fetchedRacas)
            } catch (e: Exception) {
                Log.e("AnimalRepository", "Erro ao buscar dados da rede", e)
                throw e
            }
        }
    }

    suspend fun salvarAnimal(animal: Animal): AnimalResponse {
        Log.d("AnimalRepository", "Chamando controller para salvar animal.")
        return animalController.salvarAnimal(animal)
    }

    suspend fun atualizarAnimal(animal: Animal): AnimalResponse {
        Log.d("AnimalRepository", "Chamando controller para atualizar animal ${animal.id}.")
        return animalController.atualizarAnimal(animal)
    }

    suspend fun removerAnimal(id: Long) {
        Log.d("AnimalRepository", "Chamando controller para remover animal $id.")
        return animalController.removerAnimal(id)
    }

    companion object {
        @Volatile
        private var INSTANCE: AnimalRepository? = null

        fun getInstance(context: Context): AnimalRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AnimalRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}