package br.com.caiorodri.agenpet.api.controller

import android.content.Context
import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.animal.AnimalResponse

class AnimalController(private val context: Context) {

    private val animalService = ApiClient.getAnimalService(context);

    suspend fun listarAnimaisByDonoId(idDono: Long): List<AnimalResponse> {

        Log.i("Api", "[Inicio] - listarAnimaisByUsuarioId");

        val response = animalService.listarAnimaisByDonoId(idDono = idDono);

        if (response.isSuccessful) {

            val animais = response.body()?.content!!;

            Log.i("Api", "[Sucesso] - Animais encontrados: ${animais.size}");

            Log.i("Api", "[Fim] - listarAnimaisByUsuarioId");

            return animais;


        }

        Log.e("Api", "[Erro] - listarAnimaisByUsuarioId");

        Log.i("Api", "[Fim] - listarAnimaisByUsuarioId");

        return emptyList();

    }

}