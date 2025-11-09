package br.com.caiorodri.agenpet.api.client

import android.content.Context
import br.com.caiorodri.agenpet.BuildConfig
import br.com.caiorodri.agenpet.api.service.AgendamentoService
import br.com.caiorodri.agenpet.api.service.AnimalService
import br.com.caiorodri.agenpet.api.service.UsuarioService
import br.com.caiorodri.agenpet.security.AuthInterceptor
import br.com.caiorodri.agenpet.security.SessionManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    private fun createAuthenticatedClient(context: Context): OkHttpClient {
        val sessionManager = SessionManager(context.applicationContext)
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun getRetrofitInstance(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL.plus(BuildConfig.API_NAME).plus("/"))
            .client(createAuthenticatedClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getUsuarioService(context: Context): UsuarioService {
        return getRetrofitInstance(context).create(UsuarioService::class.java)
    }

    fun getAgendamentoService(context: Context): AgendamentoService {
        return getRetrofitInstance(context).create(AgendamentoService::class.java)
    }

    fun getAnimalService(context: Context): AnimalService {
        return getRetrofitInstance(context).create(AnimalService::class.java)
    }

}