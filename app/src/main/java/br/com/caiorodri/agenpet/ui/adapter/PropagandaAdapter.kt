package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.R

class PropagandaAdapter(private val imagens: List<Int>) :
    RecyclerView.Adapter<PropagandaAdapter.PropagandaViewHolder>() {

    inner class PropagandaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view_propaganda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropagandaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_propaganda, parent, false)
        return PropagandaViewHolder(view)
    }

    override fun getItemCount(): Int = imagens.size

    override fun onBindViewHolder(holder: PropagandaViewHolder, position: Int) {
        val imagemResId = imagens[position]
        holder.imageView.setImageResource(imagemResId)
    }
}