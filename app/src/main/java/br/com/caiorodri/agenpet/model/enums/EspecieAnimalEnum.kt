package br.com.caiorodri.agenpet.model.enums

enum class EspecieAnimalEnum(val id: Int, val nome: String) {
    CACHORRO(1, "Cachorro"),
    GATO(2, "Gato"),
    AVE(3, "Ave"),
    COELHO(4, "Coelho"),
    TARTARUGA(5, "Tartaruga"),
    PEIXE(6, "Peixe"),
    CAVALO(7, "Cavalo"),
    PORQUINHO_DA_INDIA(8, "Porquinho-da-índia"),
    REPTIL(9, "Réptil"),
    RATO(10, "Rato"),
    FURAO(11, "Furão"),
    HAMSTER(12, "Hamster"),
    OUTROS(13, "Outros");

    companion object {
        fun toEnum(id: Int?): EspecieAnimalEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}