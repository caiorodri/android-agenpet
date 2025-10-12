package br.com.caiorodri.agenpet.api.controller

import android.util.Log
import br.com.caiorodri.agenpet.api.client.ApiClient
import br.com.caiorodri.agenpet.model.usuario.UsuarioLogin
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse

public class UsuarioController {

    val usuarioService = ApiClient.usuarioService;

    suspend fun findByEmailAndSenha(email: String, senha: String): UsuarioResponse? {

        Log.i("Api", "[Inicio] - findByEmailAndSenha")

        var usuario: UsuarioResponse? = null;

        val response = usuarioService.findByEmailAndSenha(UsuarioLogin(email, senha));

        Log.d("Api", "Resposta: $response")

        if(response.isSuccessful){

            usuario = response.body();

        }

        Log.i("Api", "[Fim] - findByEmailAndSenha")

        return usuario;

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

}