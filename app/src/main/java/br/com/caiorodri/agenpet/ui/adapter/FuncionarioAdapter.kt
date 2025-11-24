package br.com.caiorodri.agenpet.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.model.usuario.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

class FuncionarioAdapter(private val onClick: (Usuario) -> Unit) :
    ListAdapter<Usuario, FuncionarioAdapter.FuncionarioViewHolder>(FuncionarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuncionarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funcionario, parent, false);
        return FuncionarioViewHolder(view, onClick);
    }

    override fun onBindViewHolder(holder: FuncionarioViewHolder, position: Int) {
        holder.bind(getItem(position));
    }

    class FuncionarioViewHolder(itemView: View, val onClick: (Usuario) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val imgFoto: ShapeableImageView = itemView.findViewById(R.id.image_view_foto_funcionario);
        private val txtNome: TextView = itemView.findViewById(R.id.text_view_nome_funcionario);
        private val txtCargo: TextView = itemView.findViewById(R.id.text_view_cargo_funcionario);

        fun bind(funcionario: Usuario) {

            txtNome.text = funcionario.nome;
            txtCargo.text = funcionario.perfil?.nome ?: "N/A";

            Glide.with(itemView.context)
                .load(funcionario.urlImagem)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(imgFoto);

            itemView.setOnClickListener {
                onClick(funcionario);
            };
        }
    }

    class FuncionarioDiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id == newItem.id;
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem;
        }
    }
}