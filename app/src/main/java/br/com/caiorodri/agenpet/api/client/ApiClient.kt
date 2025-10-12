package br.com.caiorodri.agenpet.api.client

import br.com.caiorodri.agenpet.BuildConfig
import br.com.caiorodri.agenpet.api.service.AgendamentoService
import br.com.caiorodri.agenpet.api.service.AnimalService
import br.com.caiorodri.agenpet.api.service.UsuarioService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object ApiClient {

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL.plus(BuildConfig.API_NAME).plus("/"))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val usuarioService: UsuarioService by lazy {
        retrofit.create(UsuarioService::class.java)
    }

    val agendamentoService: AgendamentoService by lazy {
        retrofit.create(AgendamentoService::class.java)
    }

    val animalService: AnimalService by lazy {
        retrofit.create(AnimalService::class.java)
    }

}