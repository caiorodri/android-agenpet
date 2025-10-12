package br.com.caiorodri.agenpet.model.usuario

enum class StatusEnum(private val value: Int) {

    ATIVO(1),
    INATIVO(2);

    fun getValue(): Int {

        return value;

    }

}