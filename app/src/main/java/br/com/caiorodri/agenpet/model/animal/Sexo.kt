package br.com.caiorodri.agenpet.model.animal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sexo(
    val id: Int,
    val nome: String
) : Parcelable