package br.com.caiorodri.agenpet.model.animal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Raca(
    val id: Int,
    val nome: String,
    val especie: Especie? = null
) : Parcelable