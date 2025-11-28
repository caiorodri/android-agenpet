package br.com.caiorodri.agenpet.api.controller;

import android.content.Context;
import android.util.Log;
import br.com.caiorodri.agenpet.api.client.ApiClient;
import br.com.caiorodri.agenpet.model.usuario.Estado;
import br.com.caiorodri.agenpet.model.usuario.LoginRequest;
import br.com.caiorodri.agenpet.model.usuario.LoginResponse;
import br.com.caiorodri.agenpet.model.usuario.Status;
import br.com.caiorodri.agenpet.model.usuario.UsuarioAlterarSenha;
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest;
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest;
import br.com.caiorodri.agenpet.model.usuario.VeterinarioHorario;
import java.io.IOException;

class UsuarioController(private val context: Context) {

    private val usuarioService = ApiClient.getUsuarioService(context);
    private val TAG = "UsuarioController";

    suspend fun autenticar(loginRequest: LoginRequest): LoginResponse? {

        val endpoint = "autenticar";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.autenticar(loginRequest);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun getMeuPerfil(): UsuarioResponse? {

        val endpoint = "getMeuPerfil";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.getMeuPerfil();

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun listar(pagina: Int, itens: Int): List<UsuarioResponse> {

        val endpoint = "listar";

        Log.i(TAG, "[$endpoint] - Inicio (Pag: $pagina, Itens: $itens)");

        try {

            val response = usuarioService.listar(pagina, itens);

            if (response.isSuccessful) {

                val usuarios = response.body()?.content ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} usuários encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun recuperar(id: Long): UsuarioResponse? {

        val endpoint = "recuperar";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)");

        try {

            val response = usuarioService.recuperar(id);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun recuperarByEmail(email: String): UsuarioResponse? {

        val endpoint = "recuperarByEmail";

        Log.i(TAG, "[$endpoint] - Inicio (Email: $email)");

        try {

            val response = usuarioService.recuperarByEmail(email);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun salvar(usuario: UsuarioRequest): UsuarioResponse? {

        val endpoint = "salvar";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.salvar(usuario);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun atualizar(usuario: UsuarioUpdateRequest): LoginResponse? {

        val endpoint = "atualizar";

        Log.i(TAG, "[$endpoint] - Inicio (ID: ${usuario.id})");

        try {

            val response = usuarioService.atualizar(usuario);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun deletar(id: Long): Boolean {

        val endpoint = "deletar";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)");

        try {

            val response = usuarioService.deletar(id);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
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

        Log.i(TAG, "[$endpoint] - Fim");

        return false;
    }

    suspend fun alterarSenha(request: UsuarioAlterarSenha): Boolean {

        val endpoint = "alterarSenha";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.alterarSenha(request);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
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

        Log.i(TAG, "[$endpoint] - Fim");

        return false;
    }

    suspend fun enviarCodigoRecuperacao(email: String): Boolean {

        val endpoint = "enviarCodigoRecuperacao";

        Log.i(TAG, "[$endpoint] - Inicio (Email: $email)");

        try {

            val response = usuarioService.enviarCodigoRecuperacao(email);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
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

        Log.i(TAG, "[$endpoint] - Fim");

        return false;
    }

    suspend fun validarCodigoRecuperacao(id: Long, codigo: String): Boolean {

        val endpoint = "validarCodigoRecuperacao";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $id)");

        try {

            val response = usuarioService.validarCodigoRecuperacao(id, codigo);

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
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

        Log.i(TAG, "[$endpoint] - Fim");

        return false;
    }

    suspend fun listarStatus(): List<Status> {

        val endpoint = "listarStatus";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.listarStatus();

            if (response.isSuccessful) {

                val statusList = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${statusList.size} status encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return statusList;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarClientes(pagina: Int, itens: Int): List<UsuarioResponse> {

        val endpoint = "listarClientes";

        Log.i(TAG, "[$endpoint] - Inicio (Pag: $pagina, Itens: $itens)");

        try {

            val response = usuarioService.listarClientes(pagina, itens);

            if (response.isSuccessful) {

                val usuarios = response.body()?.content ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} clientes encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarClientes(): List<UsuarioResponse> {

        val endpoint = "listarClientes";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.listarClientes();

            if (response.isSuccessful) {

                val usuarios = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} clientes encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarRecepcionistas(): List<UsuarioResponse> {

        val endpoint = "listarRecepcionistas";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.listarRecepcionistas();

            if (response.isSuccessful) {

                val usuarios = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} recepcionistas encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarVeterinarios(): List<UsuarioResponse> {

        val endpoint = "listarVeterinarios";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.listarVeterinarios();

            if (response.isSuccessful) {

                val usuarios = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} veterinários encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarFuncionarios(pagina: Int, itens: Int): List<UsuarioResponse> {

        val endpoint = "listarFuncionarios";

        Log.i(TAG, "[$endpoint] - Inicio (Pag: $pagina, Itens: $itens)");

        try {

            val response = usuarioService.listarFuncionarios(pagina, itens);

            if (response.isSuccessful) {

                val usuarios = response.body()?.content ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} funcionários encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarFuncionariosTodos(): List<UsuarioResponse> {

        val endpoint = "listarFuncionariosTodos";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.listarFuncionariosTodos();

            if (response.isSuccessful) {

                val usuarios = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${usuarios.size} funcionários encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return usuarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun recuperarRecepcionistaAutoAtendimento(): UsuarioResponse? {

        val endpoint = "recuperarRecepcionistaAutoAtendimento";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.recuperarRecepcionistaAutoAtendimento();

            if (response.isSuccessful) {

                Log.i(TAG, "[$endpoint] - Sucesso.");
                Log.i(TAG, "[$endpoint] - Fim");

                return response.body();

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

    suspend fun listarEstados(): List<Estado> {

        val endpoint = "listarEstados";

        Log.i(TAG, "[$endpoint] - Inicio");

        try {

            val response = usuarioService.listarEstados();

            if (response.isSuccessful) {

                val estados = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${estados.size} estados encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return estados;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarHorariosVeterinario(idVeterinario: Long): List<VeterinarioHorario> {

        val endpoint = "listarHorariosVeterinario";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $idVeterinario)");

        try {

            val response = usuarioService.listarHorariosVeterinario(idVeterinario);

            if (response.isSuccessful) {

                val horarios = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${horarios.size} blocos de horário encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return horarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

    suspend fun listarHorariosDisponiveis(idVeterinario: Long, data: String, idTipo: Int): List<String> {

        val endpoint = "listarHorariosDisponiveis";

        Log.i(TAG, "[$endpoint] - Inicio (ID: $idVeterinario, Data: $data)");

        try {

            val response = usuarioService.listarHorariosDisponiveis(idVeterinario, data, idTipo);

            if (response.isSuccessful) {

                val horarios = response.body() ?: emptyList();

                Log.i(TAG, "[$endpoint] - Sucesso. ${horarios.size} slots disponíveis encontrados.");
                Log.i(TAG, "[$endpoint] - Fim");

                return horarios;

            } else {

                Log.e(TAG, "[$endpoint] - Erro na resposta: ${response.code()} - ${response.message()}");

            }

        } catch (e: IOException) {

            Log.e(TAG, "[$endpoint] - Falha de rede ou IO: ${e.message}", e);

        } catch (e: Exception) {

            Log.e(TAG, "[$endpoint] - Erro inesperado: ${e.message}", e);

        }

        Log.i(TAG, "[$endpoint] - Fim");

        return emptyList();
    }

}