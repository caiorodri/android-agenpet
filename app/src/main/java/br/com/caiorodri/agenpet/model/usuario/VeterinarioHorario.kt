package br.com.caiorodri.agenpet.model.usuario

import android.os.Parcelable;
import kotlinx.parcelize.Parcelize;

@Parcelize
data class VeterinarioHorario(
    val id: Long?,
    val idVeterinario: Long?,
    val idDiaSemana: Int,
    val horaInicio: String,
    val horaFim: String
) : Parcelable