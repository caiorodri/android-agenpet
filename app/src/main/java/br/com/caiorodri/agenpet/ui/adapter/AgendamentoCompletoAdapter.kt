package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
// ATENÇÃO: Importe o DataBinding do seu NOVO layout
import br.com.caiorodri.agenpet.databinding.ItemAgendamentoCompletoBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AgendamentoCompletoAdapter : ListAdapter<Agendamento, AgendamentoCompletoAdapter.AgendamentoCompletoViewHolder>(AgendamentoDiffCallback()) {

    inner class AgendamentoCompletoViewHolder(private val binding: ItemAgendamentoCompletoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(agendamento: Agendamento) {

            binding.textViewAnimalNome.text = agendamento.animal.nome;
            binding.textViewVetNome.text = agendamento.veterinario.nome;
            binding.textViewServicoNome.text = agendamento.tipo.nome;
            binding.textViewStatus.text = agendamento.status.nome;

            val dataCompleta = Date(agendamento.dataAgendamentoInicio);
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault());

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
        holder.bind(getItem(position))
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