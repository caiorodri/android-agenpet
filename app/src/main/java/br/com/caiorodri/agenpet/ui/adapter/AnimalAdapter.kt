package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.databinding.ItemAnimalBinding
import br.com.caiorodri.agenpet.model.animal.Animal

class AnimalAdapter(private val onItemClicked: (Animal) -> Unit) : ListAdapter<Animal, AnimalAdapter.AnimalViewHolder>(AnimalDiffCallback()) {

    inner class AnimalViewHolder(private val binding: ItemAnimalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(animal: Animal) {
            binding.textViewAnimalNome.text = animal.nome
            binding.textViewRaca.text = animal.raca?.nome ?: "Não informada"
            binding.textViewSexo.text = animal.sexo?.nome ?: "Não informado"

            itemView.setOnClickListener {
                onItemClicked(animal)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val binding = ItemAnimalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnimalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

    class AnimalDiffCallback : DiffUtil.ItemCallback<Animal>() {
        override fun areItemsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem == newItem
        }
    }
}