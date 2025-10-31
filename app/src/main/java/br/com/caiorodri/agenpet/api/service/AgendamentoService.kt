package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.api.model.PageResponse
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse
import br.com.caiorodri.agenpet.model.agendamento.Status
import br.com.caiorodri.agenpet.model.agendamento.Tipo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AgendamentoService {

    @GET("agendamentos")
    suspend fun listar(
        @Query("pagina") pagina: Int,
        @Query("quantidadeItens") quantidadeItens: Int
    ): Response<PageResponse<AgendamentoResponse>>

    @GET("agendamentos/{id}")
    suspend fun recuperar(
        @Path("id") id: Long
    ): Response<AgendamentoResponse>

    @GET("agendamentos/animal/{idAnimal}")
    suspend fun listarAgendamentosByAnimalId(
        @Path("idAnimal") idAnimal: Long,
        @Query("pagina") pagina: Int,
        @Query("quantidadeItens") quantidadeItens: Int
    ): Response<PageResponse<AgendamentoResponse>>

    @GET("agendamentos/usuario/{idUsuario}")
    suspend fun listarAgendamentosByUsuarioId(
        @Path("idUsuario") idUsuario: Long,
        @Query("pagina") pagina: Int,
        @Query("quantidadeItens") quantidadeItens: Int
    ): Response<PageResponse<AgendamentoResponse>>

    @POST("agendamentos")
    suspend fun salvar(
        @Body agendamento: AgendamentoRequest
    ): Response<AgendamentoResponse>

    @PUT("agendamentos")
    suspend fun atualizar(
        @Body agendamento: AgendamentoRequest
    ): Response<AgendamentoResponse>

    @DELETE("agendamentos/{id}")
    suspend fun deletar(
        @Path("id") id: Long
    ): Response<Void>

    @GET("agendamentos/status")
    suspend fun listarStatus(): Response<List<Status>>

    @GET("agendamentos/tipos")
    suspend fun listarTipos(): Response<List<Tipo>>

}