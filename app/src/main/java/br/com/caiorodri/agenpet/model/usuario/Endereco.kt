package br.com.caiorodri.agenpet.model.usuario

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Endereco(
    val cep: String,
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val cidade: String,
    val estado: Estado
) : Parcelable