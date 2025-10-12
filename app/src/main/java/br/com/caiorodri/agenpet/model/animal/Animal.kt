package br.com.caiorodri.agenpet.model.animal

import android.os.Parcelable
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.animal.Sexo
import br.com.caiorodri.agenpet.model.usuario.Usuario
import kotlinx.parcelize.Parcelize

@Parcelize
data class Animal(
    val agendamentos: List<Agendamento>?,
    val altura: Double?,
    val castrado: Boolean?,
    val dataNascimento: String?,
    val descricao: String?,
    val dono: Usuario,
    val id: Long?,
    val nome: String,
    val peso: Double?,
    val raca: Raca?,
    val sexo: Sexo?
): Parcelable {

    constructor(animalResponse: AnimalResponse): this (
        listOf(),
        animalResponse.altura,
        animalResponse.castrado,
        animalResponse.dataNascimento,
        animalResponse.descricao,
        Usuario(animalResponse.dono),
        animalResponse.id,
        animalResponse.nome,
        animalResponse.peso,
        animalResponse.raca,
        animalResponse.sexo
    )

}