package br.com.caiorodri.agenpet.model.animal

import android.os.Parcelable
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.usuario.Usuario
import kotlinx.parcelize.Parcelize

@Parcelize
data class Animal(
    val agendamentos: List<Agendamento>?,
    val altura: Double?,
    val castrado: Boolean?,
    val dataNascimento: Long?,
    val descricao: String?,
    val dono: Usuario,
    val id: Long?,
    val nome: String,
    val peso: Double?,
    val raca: Raca?,
    val sexo: Sexo?,
    val urlImagem: String?
): Parcelable {

    constructor(animalResponse: AnimalResponse): this (
        listOf(),
        animalResponse.altura,
        animalResponse.castrado,
        animalResponse.dataNascimento?.time,
        animalResponse.descricao,
        Usuario(animalResponse.dono),
        animalResponse.id,
        animalResponse.nome,
        animalResponse.peso,
        animalResponse.raca,
        animalResponse.sexo,
        animalResponse.urlImagem
    )

}