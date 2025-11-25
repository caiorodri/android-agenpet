package br.com.caiorodri.agenpet.model.enums

enum class DiaSemanaEnum(val id: Int, val nome: String) {
    DOMINGO(1, "Domingo"),
    SEGUNDA(2, "Segunda-feira"),
    TERCA(3, "Terça-feira"),
    QUARTA(4, "Quarta-feira"),
    QUINTA(5, "Quinta-feira"),
    SEXTA(6, "Sexta-feira"),
    SABADO(7, "Sábado");

    companion object {
        fun toEnum(id: Int?): DiaSemanaEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}