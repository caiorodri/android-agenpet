package br.com.caiorodri.agenpet.model.enums

enum class SexoAnimalEnum(val id: Int, val nome: String) {
    MACHO(1, "Macho"),
    FEMEA(2, "Femea"),
    DESCONHECIDO(3, "Desconhecido");

    companion object {
        fun toEnum(id: Int?): SexoAnimalEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}