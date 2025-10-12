package br.com.caiorodri.agenpet.model.usuario

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Estado(
    val nome: String?,
    val sigla: String
) : Parcelable