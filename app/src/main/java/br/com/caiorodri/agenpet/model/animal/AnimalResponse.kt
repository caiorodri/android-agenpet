package br.com.caiorodri.agenpet.model.animal

import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import java.util.Date

data class AnimalResponse(
    val altura: Double,
    val castrado: Boolean,
    val dataNascimento: Date?,
    val descricao: String,
    val dono: UsuarioResponse,
    val id: Long?,
    val nome: String,
    val peso: Double,
    val raca: Raca,
    val sexo: Sexo,
    val urlImagem: String?
)