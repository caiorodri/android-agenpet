package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.BuildConfig
import br.com.caiorodri.agenpet.api.model.PageResponse
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AgendamentoService {

    @GET("agendamentos/usuario/{idUsuario}")
    suspend fun listarAgendamentosByUsuarioId(
        @Path("idUsuario") idUsuario: Long,
        @Query("pagina") page: Int = BuildConfig.PAGINA_PADRAO,
        @Query("quantidadeItens") size: Int = BuildConfig.QUANTIDADE_ITENS_CONSULTA
    ): Response<PageResponse<AgendamentoResponse>>;

}