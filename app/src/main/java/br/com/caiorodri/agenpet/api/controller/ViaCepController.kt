package br.com.caiorodri.agenpet.api.controller

import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.viacep.ViaCepResponse

class ViaCepController {

    private val service = ApiClient.getViaCepService();
    private val TAG = "ViaCepController";

    suspend fun buscarCep(cep: String): ViaCepResponse? {
        try {

            val response = service.consultarCep(cep);

            if (response.isSuccessful) {

                val body = response.body();

                if (body?.erro == true) return null;

                return body;
            }

        } catch (e: Exception) {

            Log.e(TAG, "Erro ao buscar CEP", e);

        }

        return null;
    }
}