package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.model.enums.DiaSemanaEnum
import br.com.caiorodri.agenpet.model.usuario.VeterinarioHorario
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.utils.getNomeTraduzido

class HorarioAdapter(private val onDeleteClick: (VeterinarioHorario) -> Unit) : ListAdapter<VeterinarioHorario, HorarioAdapter.HorarioViewHolder>(HorarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horario, parent, false);
        return HorarioViewHolder(view, onDeleteClick);
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        holder.bind(getItem(position));
    }

    class HorarioViewHolder(itemView: View, val onDeleteClick: (VeterinarioHorario) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val txtDia: TextView = itemView.findViewById(R.id.text_dia_semana);
        private val txtHora: TextView = itemView.findViewById(R.id.text_horario);
        private val btnDel: ImageButton = itemView.findViewById(R.id.btn_deletar);

        fun bind(item: VeterinarioHorario) {

            val nomeDia = DiaSemanaEnum.toEnum(item.idDiaSemana)?.getNomeTraduzido(itemView.context) ?: DiaSemanaEnum.toEnum(item.idDiaSemana)?.nome ?: "Dia ${item.idDiaSemana}";

            txtDia.text = nomeDia;
            txtHora.text = "${item.horaInicio} - ${item.horaFim}";

            btnDel.setOnClickListener {
                onDeleteClick(item);
            }
        }
    }

    class HorarioDiffCallback : DiffUtil.ItemCallback<VeterinarioHorario>() {
        override fun areItemsTheSame(oldItem: VeterinarioHorario, newItem: VeterinarioHorario): Boolean = oldItem.id == newItem.id;
        override fun areContentsTheSame(oldItem: VeterinarioHorario, newItem: VeterinarioHorario): Boolean = oldItem == newItem;
    }
}