package br.com.caiorodri.agenpet.model.animal

import br.com.caiorodri.agenpet.model.usuario.UsuarioCadastroComplementar
import java.util.Date

data class AnimalRequest(
    val altura: Double,
    val castrado: Boolean,
    val dataNascimento: Date,
    val descricao: String,
    val dono: UsuarioCadastroComplementar,
    val id: Long?,
    val nome: String,
    val peso: Double,
    val raca: Raca,
    val sexo: Sexo,
    val urlImagem: String?

){

    constructor(animal: Animal) : this(

        altura = animal.altura!!,
        castrado = animal.castrado!!,
        dataNascimento = Date(animal.dataNascimento!!),
        descricao = animal.descricao!!,
        dono = UsuarioCadastroComplementar(
            id = animal.dono.id,
            nome = animal.dono.nome,
            email = animal.dono.email
        ),
        id = animal.id,
        nome = animal.nome,
        peso = animal.peso!!,
        raca = animal.raca!!,
        sexo = animal.sexo!!,
        urlImagem = animal.urlImagem

    )

}