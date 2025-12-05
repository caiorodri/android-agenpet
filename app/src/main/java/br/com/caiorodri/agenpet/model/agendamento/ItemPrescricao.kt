package br.com.caiorodri.agenpet.model.agendamento

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemPrescricao(
    val nomeMedicamento: String,
    val dosagem: String,
    val instrucoesUso: String
) : Parcelable