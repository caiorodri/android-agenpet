package br.com.caiorodri.agenpet.api.controller

import android.content.Context
import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.AnimalRequest
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.model.animal.Raca
import java.io.IOException

class AnimalController(private val context: Context) {

    private val animalService = ApiClient.getAnimalService(context)

    suspend fun listarAnimaisByDonoId(idDono: Long): List<AnimalResponse> {

        Log.i("Api", "[Inicio] - listarAnimaisByDonoId")

        try {

            val response = animalService.listarAnimaisByDonoId(idDono = idDono)

            if (response.isSuccessful) {

                val animais = response.body()?.content ?: emptyList()
                Log.i("Api", "[Fim - encontrados ${animais.size} animais]")

                return animais

            } else {

                val errorMsg = "Erro HTTP ${response.code()} ao listar animais."
                Log.e("Api", errorMsg)

                throw IOException(errorMsg)

            }

        } catch (e: Exception) {

            Log.e("Api", "[Erro] - listarAnimaisByDonoId: ${e.message}", e)
            throw e

        }

    }

    suspend fun listarEspecies(): List<Especie> {

        Log.i("Api", "[Inicio] - listarEspecies")

        try {

            val response = animalService.listarEspecies()

            if (response.isSuccessful) {

                val especies = response.body() ?: emptyList()
                Log.i("Api", "[Fim - encontradas ${especies.size} espécies]")

                return especies

            } else {

                val errorMsg = "Erro HTTP ${response.code()} ao listar espécies."
                Log.e("Api", errorMsg)

                throw IOException(errorMsg)

            }

        } catch (e: Exception) {

            Log.e("Api", "[Erro] - listarEspecies: ${e.message}", e)
            throw e

        }

    }

    suspend fun listarRacas(): List<Raca> {
        Log.i("Api", "[Inicio] - listarRacas")
        try {
            val response = animalService.listarRacas()

            if (response.isSuccessful) {
                val racas = response.body() ?: emptyList()
                Log.i("Api", "[Fim - encontradas ${racas.size} raças")
                return racas
            } else {
                val errorMsg = "Erro HTTP ${response.code()} ao listar raças."
                Log.e("Api", errorMsg)
                throw IOException(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("Api", "[Erro] - listarRacas: ${e.message}", e)
            throw e
        }
    }

    suspend fun salvarAnimal(animal: Animal): AnimalResponse {
        Log.i("Api", "[Inicio] - salvarAnimal: ${animal.nome}")
        try {

            val animalRequest = AnimalRequest(animal);

            val response = animalService.salvarAnimal(animalRequest);

            if (response.isSuccessful) {

                val animalSalvo = response.body();

                if (animalSalvo != null) {

                    Log.i("Api", "[Fim - animal salvo ID: ${animalSalvo.id}]");
                    return animalSalvo;

                } else {
                    throw IOException("Resposta nula do servidor ao salvar animal.");
                }

            } else {

                val errorMsg = "Erro HTTP ${response.code()} ao salvar animal.";
                Log.e("Api", errorMsg);

                throw IOException(errorMsg);

            }

        } catch (e: Exception) {

            Log.e("Api", "[Erro] - salvarAnimal: ${e.message}", e);
            throw e;

        }

    }

    suspend fun atualizarAnimal(animal: Animal): AnimalResponse {
        Log.i("Api", "[Inicio] - atualizarAnimal ID: ${animal.id}")
        try {

            val animalRequest = AnimalRequest(animal);

            val response = animalService.atualizarAnimal(animalRequest);

            if (response.isSuccessful) {

                val animalAtualizado = response.body();

                if (animalAtualizado != null) {

                    Log.i("Api", "[Fim - animal atualizado ID: ${animalAtualizado.id}]");
                    return animalAtualizado;

                } else {

                    throw IOException("Resposta nula do servidor ao atualizar animal.");

                }

            } else {

                val errorMsg = "Erro HTTP ${response.code()} ao atualizar animal.";
                Log.e("Api", errorMsg);

                throw IOException(errorMsg);

            }

        } catch (e: Exception) {

            Log.e("Api", "[Erro] - atualizarAnimal: ${e.message}", e);
            throw e;

        }

    }

    suspend fun removerAnimal(id: Long) {

        Log.i("Api", "[Inicio] - removerAnimal ID: $id")

        try {

            val response = animalService.removerAnimal(id)

            if (response.isSuccessful) {

                Log.i("Api", "[Fim - animal removido ID: $id]")
                return

            } else {

                val errorMsg = "Erro HTTP ${response.code()} ao remover animal."
                Log.e("Api", errorMsg)

                throw IOException(errorMsg)

            }

        } catch (e: Exception) {

            Log.e("Api", "[Erro] - removerAnimal: ${e.message}", e)
            throw e

        }

    }

}
