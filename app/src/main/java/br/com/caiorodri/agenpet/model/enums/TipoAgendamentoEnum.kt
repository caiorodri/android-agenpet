package br.com.caiorodri.agenpet.model.enums

enum class TipoAgendamentoEnum(val id: Int, val nome: String, val duracaoMinutos: Int) {
    CONSULTA(1, "Consulta", 30),
    CIRURGIA(2, "Cirurgia", 60);

    companion object {
        fun toEnum(id: Int?): TipoAgendamentoEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}