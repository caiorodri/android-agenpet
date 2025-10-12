package br.com.caiorodri.agenpet.model.usuario

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Perfil(
    val id: Int,
    val nome: String?
) : Parcelable