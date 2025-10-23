package br.com.caiorodri.agenpet.api.controller

import android.content.Context
import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.usuario.LoginRequest
import br.com.caiorodri.agenpet.model.usuario.LoginResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse

public class UsuarioController(private val context: Context) {

    private val usuarioService = ApiClient.getUsuarioService(context)

    suspend fun autenticar(loginRequest: LoginRequest): LoginResponse? {

        Log.i("Api", "[Inicio] - findByEmailAndSenha")

        try {
            val response = usuarioService.autenticar(loginRequest)
            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.i("Api", "[Fim] - findByEmailAndSenha")

        return null;

    }

    suspend fun save(usuario: UsuarioRequest): UsuarioResponse? {

        Log.i("Api", "[Inicio] - save")

        var usuarioSalvo: UsuarioResponse? = null;

        val response = usuarioService.salvar(usuario);

        if(response.isSuccessful){

            Log.i("Api", "Sucesso: ${response.code()} - Body: ${response.body()}")
            usuarioSalvo = response.body();

        } else {

            val errorBody = response.errorBody()?.string()
            Log.e("Api", "Erro: ${response.code()} - Mensagem: ${response.message()}")
            Log.e("Api", "Corpo do Erro: $errorBody")

        }

        Log.i("Api", "[Fim] - save")

        return usuarioSalvo;

    }

    suspend fun getMeuPerfil(): UsuarioResponse? {
        try {
            val response = usuarioService.getMeuPerfil()
            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}