package br.com.caiorodri.agenpet.api.repository;

import android.content.Context;
import android.util.Log;
import br.com.caiorodri.agenpet.api.controller.AnimalController;
import br.com.caiorodri.agenpet.api.controller.UsuarioController
import br.com.caiorodri.agenpet.model.animal.Animal;
import br.com.caiorodri.agenpet.model.animal.AnimalResponse;
import br.com.caiorodri.agenpet.model.animal.Especie;
import br.com.caiorodri.agenpet.model.animal.Raca;
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import kotlinx.coroutines.async;
import kotlinx.coroutines.coroutineScope;
import kotlinx.coroutines.sync.Mutex;
import kotlinx.coroutines.sync.withLock;

data class AnimalData(
    val especies: List<Especie>,
    val racas: List<Raca>,
    val clientes: List<UsuarioResponse>? = null
)

class AnimalRepository private constructor(context: Context) {

    private val animalController = AnimalController(context.applicationContext);
    private val usuarioController = UsuarioController(context.applicationContext);
    private var especiesCache: List<Especie>? = null;
    private var racasCache: List<Raca>? = null;
    private var clientesCache: List<UsuarioResponse>? = null;
    private val mutex = Mutex();

    suspend fun getAnimalData(isRecepcionista: Boolean): AnimalData {

        mutex.withLock {

            if (especiesCache != null && racasCache != null) {

                if (isRecepcionista) {

                    if (clientesCache != null) {

                        Log.i("AnimalRepository", "Retornando dados (incluindo clientes) do cache.");
                        return AnimalData(especiesCache!!, racasCache!!, clientesCache);

                    }

                } else {

                    Log.i("AnimalRepository", "Retornando dados do cache.");
                    return AnimalData(especiesCache!!, racasCache!!, null);

                }

            }

            Log.i("AnimalRepository", "Buscando dados da rede.");

            try {

                return coroutineScope {

                    val especiesJob = async { animalController.listarEspecies() }
                    val racasJob = async { animalController.listarRacas() }
                    val clientesJob = if (isRecepcionista) async { usuarioController.listarClientes() } else null

                    val especies = especiesJob.await();
                    val racas = racasJob.await();
                    val clientes = clientesJob?.await();

                    especiesCache = especies;
                    racasCache = racas;

                    if (isRecepcionista) clientesCache = clientes;

                    AnimalData(especies, racas, clientes);

                }

            } catch (e: Exception) {

                Log.e("AnimalRepository", "Erro ao buscar dados da rede", e);
                throw e;

            }
        }
    }

    suspend fun salvarAnimal(animal: Animal): AnimalResponse? {

        Log.d("AnimalRepository", "Chamando controller para salvar animal.");
        return animalController.salvarAnimal(animal);

    }

    suspend fun atualizarAnimal(animal: Animal): AnimalResponse? {

        Log.d("AnimalRepository", "Chamando controller para atualizar animal ${animal.id}.");
        return animalController.atualizarAnimal(animal);

    }

    suspend fun removerAnimal(id: Long) {

        Log.d("AnimalRepository", "Chamando controller para remover animal $id.");
        animalController.removerAnimal(id);

        return;

    }

    companion object {
        @Volatile
        private var INSTANCE: AnimalRepository? = null;

        fun getInstance(context: Context): AnimalRepository {

            return INSTANCE ?: synchronized(this) {

                val instance = AnimalRepository(context);
                INSTANCE = instance;
                instance;

            }
        }
    }
}