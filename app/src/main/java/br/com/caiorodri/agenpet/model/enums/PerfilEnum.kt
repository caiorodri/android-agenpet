package br.com.caiorodri.agenpet.model.enums

enum class PerfilEnum(val id: Int, val nome: String) {
    CLIENTE(1, "Cliente"),
    RECEPCIONISTA(2, "Recepcionista"),
    VETERINARIO(3, "Veterinario"),
    ADMINISTRADOR(4, "Administrador");

    companion object {
        fun toEnum(id: Int?): PerfilEnum? {
            if (id == null) return null
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Id inválido: $id")
        }
    }
}