package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.model.propaganda.Propaganda

class PropagandaAdapter(private val itens: List<Propaganda>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<PropagandaAdapter.PropagandaViewHolder>() {

    inner class PropagandaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view_propaganda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropagandaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_propaganda, parent, false)
        return PropagandaViewHolder(view)
    }

    override fun getItemCount(): Int = itens.size;

    override fun onBindViewHolder(holder: PropagandaViewHolder, position: Int) {
        val item = itens[position]

        holder.imageView.setImageResource(item.imagemResId);

        holder.itemView.setOnClickListener {
            onItemClick(item.url);
        }
    }
}