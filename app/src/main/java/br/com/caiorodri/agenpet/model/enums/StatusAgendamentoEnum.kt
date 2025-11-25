package br.com.caiorodri.agenpet.model.enums

enum class StatusAgendamentoEnum(val id: Int, val nome: String) {
    ABERTO(1, "Aberto"),
    CANCELADO(2, "Cancelado"),
    CONCLUIDO(3, "Concluido"),
    PERDIDO(4, "Perdido");

    companion object {
        fun toEnum(id: Int?): StatusAgendamentoEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}