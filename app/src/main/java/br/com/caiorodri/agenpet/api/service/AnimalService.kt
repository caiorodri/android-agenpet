package br.com.caiorodri.agenpet.api.service

import br.com.caiorodri.agenpet.BuildConfig
import br.com.caiorodri.agenpet.api.model.PageResponse
import br.com.caiorodri.agenpet.model.animal.AnimalRequest
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.model.animal.Raca
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimalService {

    @POST("animais")
    suspend fun salvarAnimal(@Body animal: AnimalRequest): Response<AnimalResponse>

    @PUT("animais")
    suspend fun atualizarAnimal(@Body animal: AnimalRequest): Response<AnimalResponse>

    @DELETE("animais/{id}")
    suspend fun removerAnimal(@Path("id") id: Long): Response<Unit>

    @GET("animais/dono/{idDono}")
    suspend fun listarAnimaisByDonoId(
        @Path("idDono") idDono: Long,
        @Query("pagina") page: Int = BuildConfig.PAGINA_PADRAO,
        @Query("quantidadeItens") size: Int = BuildConfig.QUANTIDADE_ITENS_CONSULTA
    ): Response<PageResponse<AnimalResponse>>;

    @GET("animais/especies")
    suspend fun listarEspecies(): Response<List<Especie>>;

    @GET("animais/racas")
    suspend fun listarRacas(): Response<List<Raca>>;


}