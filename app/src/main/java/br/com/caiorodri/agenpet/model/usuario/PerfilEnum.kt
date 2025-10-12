package br.com.caiorodri.agenpet.model.usuario

enum class PerfilEnum(private val value: Int){

    CLIENTE(1),
    RECEPCIONISTA(2),
    VETERINARIO(3),
    ADMINISTRADOR(4);

    fun getValue(): Int{
        return value;
    }

}