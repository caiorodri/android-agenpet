package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.BuildConfig
import br.com.caiorodri.agenpet.api.model.PageResponse
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimalService {

    @GET("animais/dono/{idDono}")
    suspend fun listarAnimaisByDonoId(
        @Path("idDono") idDono: Long,
        @Query("pagina") page: Int = BuildConfig.PAGINA_PADRAO,
        @Query("quantidadeItens") size: Int = BuildConfig.QUANTIDADE_ITENS_CONSULTA
    ): Response<PageResponse<AnimalResponse>>;

}