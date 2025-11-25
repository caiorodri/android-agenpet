package br.com.caiorodri.agenpet.model.enums

enum class StatusUsuarioEnum(val id: Int, val nome: String) {
    ATIVO(1, "Ativo"),
    INATIVO(2, "Inativo");

    companion object {
        fun toEnum(id: Int?): StatusUsuarioEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}