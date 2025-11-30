package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.model.viacep.ViaCepResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {

    @GET("{cep}/json/")
    suspend fun consultarCep(@Path("cep") cep: String): Response<ViaCepResponse>

}