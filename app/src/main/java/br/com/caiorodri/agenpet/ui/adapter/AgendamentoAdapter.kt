package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.databinding.ItemAgendamentoBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AgendamentoAdapter : ListAdapter<Agendamento, AgendamentoAdapter.AgendamentoViewHolder>(AgendamentoDiffCallback()) {

    inner class AgendamentoViewHolder(private val binding: ItemAgendamentoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(agendamento: Agendamento) {

            binding.textViewAnimalNome.text = agendamento.animal.nome
            binding.textViewVetNome.text = agendamento.veterinario.nome

            val dataCompleta = Date(agendamento.dataAgendamentoInicio);

            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault());

            try {
                binding.textViewDataAgendamento.text = outputFormat.format(dataCompleta);
                binding.textViewHorarioAgendamento.text = formatoHora.format(dataCompleta);
            } catch (e: Exception) {
                binding.textViewDataAgendamento.text = "Data Inválida"
                binding.textViewHorarioAgendamento.text = "Horário Inválido"
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendamentoViewHolder {
        val binding = ItemAgendamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AgendamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendamentoViewHolder, position: Int) {
        val agendamento = getItem(position)
        holder.bind(agendamento)
    }
}