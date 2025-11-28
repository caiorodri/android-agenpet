package br.com.caiorodri.agenpet.api.controller;

import android.content.Context;
import android.util.Log;
import br.com.caiorodri.agenpet.api.client.ApiClient;
import br.com.caiorodri.agenpet.model.animal.Animal;
import br.com.caiorodri.agenpet.model.animal.AnimalRequest;
import br.com.caiorodri.agenpet.model.animal.AnimalResponse;
import br.com.caiorodri.agenpet.model.animal.Especie;
import br.com.caiorodri.agenpet.model.animal.Raca;
import java.io.IOException;

class AnimalController(private val context: Context) {

    private val animalService = ApiClient.getAnimalService(context);
    private val TAG = "AnimalController";

    suspend fun recuperarById(id: Long): AnimalResponse? {

        val endpoint = "recuperarById";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)");

        try {

            val response = animalService.recuperarById(id = id);

            if (response.isSuccessful) {

                val animal: AnimalResponse? = response.body();

                Log.i(TAG, "[$endpoint] - Sucesso. animal com id ${id} encontrado.");
                Log.i(TAG, "[$endpoint] - Fim");

                return animal;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return null;
    }

    suspend fun listar(): List<AnimalResponse> {

        val endpoint = "listar";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = animalService.listar();

            if (response.isSuccessful) {

                val animais = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${animais.size} animais encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return animais;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)");

        return emptyList();
    }

    suspend fun listarAnimaisByDonoId(idDono: Long): List<AnimalResponse> {

        val endpoint = "listarAnimaisByDonoId";

        Log.i(TAG, "[$endpoint] - Inicio (DonoID: $idDono)");

        try {

            val response = animalService.listarAnimaisByDonoId(idDono = idDono);

            if (response.isSuccessful) {

                val animais = response.body()?.content ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${animais.size} animais encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return animais;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)");

        return emptyList();
    }

    suspend fun listarEspecies(): List<Especie> {

        val endpoint = "listarEspecies";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = animalService.listarEspecies();

            if (response.isSuccessful) {

                val especies = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${especies.size} espécies encontradas.");
                Log.i(TAG, "[$endpoint] - Fim");

                return especies;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)");

        return emptyList();
    }

    suspend fun listarRacas(): List<Raca> {

        val endpoint = "listarRacas";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = animalService.listarRacas();

            if (response.isSuccessful) {

                val racas = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${racas.size} raças encontradas.");
                Log.i(TAG, "[$endpoint] - Fim");

                return racas;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando lista vazia)");

        return emptyList();
    }

    suspend fun salvarAnimal(animal: Animal): AnimalResponse? {

        val endpoint = "salvarAnimal";

        Log.i(TAG, "[$endpoint] - Inicio (Nome: ${animal.nome})");

        try {

            val animalRequest = AnimalRequest(animal);
            val response = animalService.salvarAnimal(animalRequest);

            if (response.isSuccessful) {

                val animalSalvo = response.body();

                if (animalSalvo != null) {

                    Log.i(TAG, "[$endpoint] - Sucesso. Animal salvo ID: ${animalSalvo.id}.");
                    Log.i(TAG, "[$endpoint] - Fim");

                    return animalSalvo;
                }

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return null;
    }

    suspend fun atualizarAnimal(animal: Animal): AnimalResponse? {

        val endpoint = "atualizarAnimal";

        Log.i(TAG, "[$endpoint] - Inicio (ID: ${animal.id})");

        try {

            val animalRequest = AnimalRequest(animal);
            val response = animalService.atualizarAnimal(animalRequest);

            if (response.isSuccessful) {

                val animalAtualizado = response.body();

                if (animalAtualizado != null) {

                    Log.i(TAG, "[$endpoint] - Sucesso. Animal atualizado ID: ${animalAtualizado.id}.");
                    Log.i(TAG, "[$endpoint] - Fim");

                    return animalAtualizado;
                }

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return null;
    }

    suspend fun removerAnimal(id: Long): Boolean {

        val endpoint = "removerAnimal";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)");

        try {

            val response = animalService.removerAnimal(id);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso. Animal removido.");
                Log.i(TAG, "[$endpoint] - Fim");

                return true;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim (Retornando false)");

        return false;
    }
}