package br.com.caiorodri.agenpet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.model.sobre.Desenvolvedor

class DesenvolvedorAdapter(
    private val desenvolvedores: List<Desenvolvedor>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<DesenvolvedorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoto: ImageView = view.findViewById(R.id.img_dev_foto);
        val txtNome: TextView = view.findViewById(R.id.txt_dev_nome);
        val txtFuncao: TextView = view.findViewById(R.id.txt_dev_funcao);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_desenvolvedor, parent, false);
        return ViewHolder(view);
    }

    override fun getItemCount() = desenvolvedores.size;

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dev = desenvolvedores[position];

        holder.txtNome.text = dev.nome;
        holder.txtFuncao.text = dev.funcao;

        holder.imgFoto.setImageResource(dev.imagemResId);

        holder.itemView.setOnClickListener {
            onClick(dev.linkedinUrl);
        }
    }
}