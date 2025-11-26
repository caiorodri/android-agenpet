package br.com.caiorodri.agenpet.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.ItemAgendamentoCompletoBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.enums.StatusAgendamentoEnum
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AgendamentoCompletoAdapter (
    private val onItemClicked: (Agendamento) -> Unit
): ListAdapter<Agendamento, AgendamentoCompletoAdapter.AgendamentoCompletoViewHolder>(AgendamentoDiffCallback()) {

    inner class AgendamentoCompletoViewHolder(private val binding: ItemAgendamentoCompletoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(agendamento: Agendamento) {

            val context = binding.root.context;

            binding.textViewAnimalNome.text = agendamento.animal.nome;
            binding.textViewVetNome.text = agendamento.veterinario.nome;

            val dataCompleta = Date(agendamento.dataAgendamentoInicio);
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault());

            binding.textViewServicoNome.text = agendamento.tipo.getNomeTraduzido(context);
            binding.textViewStatus.text = agendamento.status.getNomeTraduzido(context);

            val (corFundoRes, corTextoRes) = when (agendamento.status.id) {
                StatusAgendamentoEnum.CANCELADO.id -> Pair(R.color.status_cancelado_fundo, R.color.status_cancelado_texto);
                StatusAgendamentoEnum.CONCLUIDO.id -> Pair(R.color.status_finalizado_fundo, R.color.status_finalizado_texto);
                StatusAgendamentoEnum.PERDIDO.id -> Pair(R.color.status_perdido_fundo, R.color.status_perdido_texto);
                else -> Pair(R.color.status_aberto_fundo, R.color.status_aberto_texto);
            }

            val corFundo = ContextCompat.getColor(context, corFundoRes)
            val corTexto = ContextCompat.getColor(context, corTextoRes)

            binding.textViewStatus.chipBackgroundColor = ColorStateList.valueOf(corFundo)
            binding.textViewStatus.setTextColor(corTexto)

            try {

                binding.textViewDataAgendamento.text = outputFormat.format(dataCompleta);
                binding.textViewHorarioAgendamento.text = formatoHora.format(dataCompleta);

            } catch (e: Exception) {

                binding.textViewDataAgendamento.text = "Data Inválida";
                binding.textViewHorarioAgendamento.text = "Horário Inválido";

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendamentoCompletoViewHolder {
        val binding = ItemAgendamentoCompletoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AgendamentoCompletoViewHolder(binding);
    }

    override fun onBindViewHolder(holder: AgendamentoCompletoViewHolder, position: Int) {

        val agendamento = getItem(position)

        holder.bind(getItem(position))

        holder.itemView.setOnClickListener {
            onItemClicked(agendamento)
        }
    }

    class AgendamentoDiffCallback : DiffUtil.ItemCallback<Agendamento>() {
        override fun areItemsTheSame(oldItem: Agendamento, newItem: Agendamento): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Agendamento, newItem: Agendamento): Boolean {
            return oldItem == newItem
        }
    }
}