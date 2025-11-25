package br.com.caiorodri.agenpet.model.enums

enum class EstadoEnum(val sigla: String, val nome: String) {
    AC("AC", "Acre"),
    AL("AL", "Alagoas"),
    AM("AM", "Amazonas"),
    AP("AP", "Amapá"),
    BA("BA", "Bahia"),
    CE("CE", "Ceará"),
    DF("DF", "Distrito Federal"),
    ES("ES", "Espírito Santo"),
    GO("GO", "Goiás"),
    MA("MA", "Maranhão"),
    MG("MG", "Minas Gerais"),
    MS("MS", "Mato Grosso do Sul"),
    MT("MT", "Mato Grosso"),
    PA("PA", "Pará"),
    PB("PB", "Paraíba"),
    PE("PE", "Pernambuco"),
    PI("PI", "Piauí"),
    PR("PR", "Paraná"),
    RJ("RJ", "Rio de Janeiro"),
    RN("RN", "Rio Grande do Norte"),
    RS("RS", "Rio Grande do Sul"),
    RO("RO", "Rondônia"),
    RR("RR", "Roraima"),
    SC("SC", "Santa Catarina"),
    SP("SP", "São Paulo"),
    SE("SE", "Sergipe"),
    TO("TO", "Tocantins");

    companion object {
        fun toEnum(sigla: String?): EstadoEnum? {
            if (sigla.isNullOrBlank()) return null
            return entries.find { it.sigla.equals(sigla, ignoreCase = true) }
                ?: throw IllegalArgumentException("Sigla inválida: $sigla")
        }
    }
}