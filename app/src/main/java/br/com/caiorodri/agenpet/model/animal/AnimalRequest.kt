package br.com.caiorodri.agenpet.model.animal

import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.animal.Sexo
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import java.util.Date

data class AnimalRequest(
    val altura: Double,
    val castrado: Boolean,
    val dataNascimento: Date,
    val descricao: String,
    val dono: UsuarioCadastroAnimal,
    val id: Long?,
    val nome: String,
    val peso: Double,
    val raca: Raca,
    val sexo: Sexo

){

    constructor(animal: Animal) : this(

        altura = animal.altura!!,
        castrado = animal.castrado!!,
        dataNascimento = Date(animal.dataNascimento!!),
        descricao = animal.descricao!!,
        dono = UsuarioCadastroAnimal(
            id = animal.dono.id,
            nome = animal.dono.nome,
            email = animal.dono.email
        ),
        id = animal.id,
        nome = animal.nome,
        peso = animal.peso!!,
        raca = animal.raca!!,
        sexo = animal.sexo!!

    )

}