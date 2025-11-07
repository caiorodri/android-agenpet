package br.com.caiorodri.agenpet.model.agendamento

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tipo(
    val id: Int,
    val nome: String,
    val duracaoMinutos: Int
): Parcelable