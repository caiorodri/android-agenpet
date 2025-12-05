package br.com.caiorodri.agenpet.ui.agendamento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController;
import androidx.navigation.fragment.navArgs;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.databinding.FragmentPosConsultaCadastroBinding;
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.ItemPrescricao
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

class PosConsultaCadastroFragment : Fragment() {

    private var _binding: FragmentPosConsultaCadastroBinding? = null;
    private val binding get() = _binding!!;
    private val args: PosConsultaFragmentArgs by navArgs();
    private val viewModel: PosConsultaCadastroViewModel by viewModels();
    private lateinit var adapter: PrescricaoAdapter;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosConsultaCadastroBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_finalizar_consulta);

        val agendamento = args.agendamento;
        setupHeader(agendamento);
        setupRecyclerView();
        setupListeners(agendamento);
        setupObservers();
    }

    private fun setupHeader(agendamento: Agendamento) {

        binding.txtNomePet.text = agendamento.animal.nome;

        Glide.with(this)
            .load(agendamento.animal.urlImagem)
            .placeholder(R.drawable.ic_pet)
            .circleCrop()
            .into(binding.imgPetFoto);

    }

    private fun setupRecyclerView() {

        adapter = PrescricaoAdapter { itemParaRemover ->
            viewModel.removerPrescricao(itemParaRemover);
        };

        binding.recyclerPrescricoes.layoutManager = LinearLayoutManager(context);
        binding.recyclerPrescricoes.adapter = adapter;

    }

    private fun setupListeners(agendamento: Agendamento) {

        binding.btnAddMedicamento.setOnClickListener {
            mostrarDialogAddMedicamento();
        };

        binding.btnFinalizar.setOnClickListener {
            val diagnostico = binding.editDiagnostico.text.toString();
            val observacoes = binding.editObservacoes.text.toString();

            viewModel.finalizarConsulta(agendamento, diagnostico, observacoes);
        };
    }

    private fun setupObservers() {

        viewModel.prescricoes.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista);
            binding.txtSemRemedios.isVisible = lista.isEmpty();
        };

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.loadingOverlay.isVisible = loading;
            binding.btnFinalizar.isEnabled = !loading;
        };

        viewModel.sucesso.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, getString(R.string.sucesso_consulta_finalizada), Toast.LENGTH_LONG).show();

                val result = Bundle().apply {
                    putBoolean("should_refresh", true);
                }

                parentFragmentManager.setFragmentResult("agendamento_request", result);

                findNavController().popBackStack();
            }
        };

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro != null) {
                Toast.makeText(context, erro, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private fun mostrarDialogAddMedicamento() {

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_medicamento, null);

        val editNome = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edit_dialog_nome);
        val editDose = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edit_dialog_dose);
        val editInstr = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edit_dialog_instrucoes);

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_add_remedio_titulo))
            .setView(view)
            .setPositiveButton(getString(R.string.btn_salvar)) { _, _ ->
                val nome = editNome.text.toString();
                val dose = editDose.text.toString();
                val instr = editInstr.text.toString();

                if (nome.isNotBlank() && dose.isNotBlank()) {
                    viewModel.adicionarPrescricao(
                        ItemPrescricao(
                            nomeMedicamento = nome,
                            dosagem = dose,
                            instrucoesUso = instr
                        )
                    );
                } else {
                    Toast.makeText(context, getString(R.string.erro_campos_medicamento_obrigatorios), Toast.LENGTH_SHORT).show();
                }
            }
            .setNegativeButton(getString(R.string.btn_cancelar), null)
            .show();
    }

    inner class PrescricaoAdapter(private val onDelete: (ItemPrescricao) -> Unit) :
        RecyclerView.Adapter<PrescricaoAdapter.ViewHolder>() {

        private var items: List<ItemPrescricao> = emptyList();

        fun submitList(newItems: List<ItemPrescricao>) {
            items = ArrayList(newItems);
            notifyDataSetChanged();
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prescricao_removivel, parent, false);
            return ViewHolder(view);
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position];
            holder.nome.text = item.nomeMedicamento;
            holder.detalhes.text = "${item.dosagem} • ${item.instrucoesUso}";
            holder.btnDelete.setOnClickListener { onDelete(item); };
        }

        override fun getItemCount() = items.size;

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nome: TextView = view.findViewById(R.id.txt_nome_medicamento);
            val detalhes: TextView = view.findViewById(R.id.txt_detalhes);
            val btnDelete: ImageButton = view.findViewById(R.id.btn_remover);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}