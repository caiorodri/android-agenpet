package br.com.caiorodri.agenpet.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.ItemAnimalBinding
import br.com.caiorodri.agenpet.model.animal.Animal
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class AnimalAdapter(private val onItemClicked: (Animal) -> Unit) : ListAdapter<Animal, AnimalAdapter.AnimalViewHolder>(AnimalDiffCallback()) {

    private val DESCONHECIDO = "Desconhecido";

    inner class AnimalViewHolder(private val binding: ItemAnimalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(animal: Animal) {

            binding.textViewAnimalNome.text = animal.nome;

            if(animal.raca?.especie?.nome != DESCONHECIDO){

                val text = animal.raca?.especie?.nome + " - " + animal.raca?.nome;

                binding.textViewRaca.text = text;

            } else {

                binding.textViewRaca.text = animal.raca.nome;

            }

            binding.textViewSexo.text = animal.sexo?.nome ?: "Não informado";

            val context = itemView.context;

            val iconResId = when (animal.sexo?.id){

                1 -> R.drawable.ic_male
                2 -> R.drawable.ic_female
                else -> R.drawable.ic_question

            }

            val drawable = androidx.core.content.ContextCompat.getDrawable(context, iconResId);

            if(drawable != null){

                val iconSize = (binding.textViewSexo.textSize).toInt() + ((binding.textViewSexo.textSize).toInt() / 2);

                drawable.setBounds(0, 0, iconSize, iconSize)

                val tintColor = binding.textViewSexo.currentTextColor;
                drawable.setTint(tintColor);

                binding.textViewSexo.setCompoundDrawables(drawable, null, null, null);

            } else {
                binding.textViewSexo.setCompoundDrawables(null, null, null, null)
            }

            binding.progressBarFotoItem.visibility = View.VISIBLE;

            Glide.with(itemView.context)
                .load(animal.urlImagem)
                .placeholder(R.drawable.ic_pet)
                .error(R.drawable.ic_pet)
                .circleCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBarFotoItem.visibility = View.GONE
                        return false;
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBarFotoItem.visibility = View.GONE;
                        return false;
                    }
                })
                .into(binding.imageViewFotoAnimal)

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