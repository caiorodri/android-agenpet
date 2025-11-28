package br.com.caiorodri.agenpet.api.repository;

import android.content.Context;
import android.util.Log;
import br.com.caiorodri.agenpet.api.controller.AgendamentoController;
import br.com.caiorodri.agenpet.api.controller.AnimalController
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest;
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse;
import br.com.caiorodri.agenpet.model.agendamento.Status;
import br.com.caiorodri.agenpet.model.agendamento.Tipo;
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import kotlinx.coroutines.async;
import kotlinx.coroutines.coroutineScope;
import kotlinx.coroutines.sync.Mutex;
import kotlinx.coroutines.sync.withLock;
import java.io.IOException;

data class AgendamentoData(
    val tipos: List<Tipo>,
    val status: List<Status>,
    val veterinarios: List<UsuarioResponse>,
    val recepcionista: UsuarioResponse?,
    val clientes: List<UsuarioResponse>?,
    val animais: List<AnimalResponse>?
);

class AgendamentoRepository private constructor(context: Context) {

    private val agendamentoController = AgendamentoController(context.applicationContext);
    private val usuarioController = UsuarioController(context.applicationContext);
    private val animalController = AnimalController(context.applicationContext);

    private var tiposCache: List<Tipo>? = null;
    private var statusCache: List<Status>? = null;
    private var veterinariosCache: List<UsuarioResponse>? = null;
    private var recepcionistaCache: UsuarioResponse? = null;
    private var clientesCache: List<UsuarioResponse>? = null;
    private var animaisCache: List<AnimalResponse>? = null;
    private val mutex = Mutex();

    suspend fun getAgendamentoData(isRecepcionista: Boolean): AgendamentoData {

        mutex.withLock {

            if(!isRecepcionista && tiposCache != null && statusCache != null && veterinariosCache != null && recepcionistaCache != null) {
                    Log.i("AgendamentoRepository", "Retornando dados de agendamento do cache.");
                    return AgendamentoData(tiposCache!!, statusCache!!, veterinariosCache!!, recepcionistaCache, clientesCache, animaisCache);
                }

            }

            if(isRecepcionista && tiposCache != null && statusCache != null && veterinariosCache != null && animaisCache != null && clientesCache != null) {
                Log.i("AgendamentoRepository", "Retornando dados de agendamento do cache.");
                return AgendamentoData(tiposCache!!, statusCache!!, veterinariosCache!!, null, clientesCache, animaisCache);
            }

            Log.i("AgendamentoRepository", "Buscando dados de agendamento da rede.");

            try {

                return coroutineScope {

                    val tiposJob = async { agendamentoController.listarTipos(); };
                    val statusJob = async { agendamentoController.listarStatus(); };
                    val vetsJob = async { usuarioController.listarVeterinarios(); };

                    val recepJob = if (!isRecepcionista) async { usuarioController.recuperarRecepcionistaAutoAtendimento() } else null;
                    val clientesJob = if (isRecepcionista) async { usuarioController.listarClientes() } else null;
                    val animaisJob = if (isRecepcionista) async { animalController.listar() } else null

                    val tipos = tiposJob.await();
                    val status = statusJob.await();
                    val vets = vetsJob.await();
                    val recepcionista = recepJob?.await();
                    val clientes = clientesJob?.await()
                    val animais = animaisJob?.await()

                    tiposCache = tipos;
                    statusCache = status;
                    veterinariosCache = vets;
                    recepcionistaCache = recepcionista;
                    clientesCache = clientes;
                    animaisCache = animais;

                    AgendamentoData(tipos, status, vets, recepcionista, clientes, animais);
                };

            } catch (e: Exception) {
                Log.e("AgendamentoRepository", "Erro ao buscar dados da rede", e);
                throw e;
            }
        }


    suspend fun salvarAgendamento(request: AgendamentoRequest): AgendamentoResponse {
        Log.d("AgendamentoRepository", "Chamando controller para salvar agendamento.");
        return agendamentoController.salvarAgendamento(request)
            ?: throw IOException("Resposta nula do servidor ao salvar agendamento.");
    }

    suspend fun atualizarAgendamento(request: AgendamentoRequest): AgendamentoResponse {
        Log.d("AgendamentoRepository", "Chamando controller para atualizar agendamento ${request.id}.");
        return agendamentoController.atualizarAgendamento(request)
            ?: throw IOException("Resposta nula do servidor ao atualizar agendamento.");
    }

    suspend fun listarHorariosDisponiveis(idVeterinario: Long, data: String, idTipo: Int): List<String> {
        Log.d("AgendamentoRepository", "Chamando controller para listar horários disponíveis.");
        return usuarioController.listarHorariosDisponiveis(idVeterinario, data, idTipo);
    }

    companion object {
        @Volatile
        private var INSTANCE: AgendamentoRepository? = null;

        fun getInstance(context: Context): AgendamentoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AgendamentoRepository(context);
                INSTANCE = instance;
                instance;
            }
        }
    }
}