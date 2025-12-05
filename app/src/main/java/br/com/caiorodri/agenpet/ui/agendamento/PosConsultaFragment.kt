package br.com.caiorodri.agenpet.ui.agendamento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.navArgs;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.databinding.FragmentPosConsultaBinding;
import br.com.caiorodri.agenpet.model.agendamento.Agendamento;
import br.com.caiorodri.agenpet.model.agendamento.ResultadoConsulta
import br.com.caiorodri.agenpet.model.agendamento.ResultadoConsultaResponse
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class PosConsultaFragment : Fragment() {

    private var _binding: FragmentPosConsultaBinding? = null;
    private val binding get() = _binding!!;
    private val args: PosConsultaFragmentArgs by navArgs();
    private val viewModel: PosConsultaViewModel by viewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosConsultaBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_pos_consulta);

        val agendamento = args.agendamento;
        setupHeader(agendamento);
        setupObservers();
        setupListeners();

        viewModel.carregarResultadoConsulta(agendamento.id!!);
    }

    private fun setupHeader(agendamento: Agendamento) {
        binding.txtNomePetResultado.text = agendamento.animal.nome;

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        val dataStr = try {
            dateFormat.format(Date(agendamento.dataAgendamentoInicio));
        } catch (e: Exception) {
            "--/--/----";
        }

        binding.txtDataVetResultado.text = "$dataStr • ${agendamento.veterinario.nome}";

        Glide.with(this)
            .load(agendamento.animal.urlImagem)
            .placeholder(R.drawable.ic_pet)
            .error(R.drawable.ic_pet)
            .circleCrop()
            .into(binding.imgPetFotoResultado);
    }

    private fun setupObservers() {
        viewModel.resultadoConsulta.observe(viewLifecycleOwner) { resultado ->
            if (resultado != null) {
                popularDadosMedicos(resultado);
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.isVisible = isLoading;
        }

        viewModel.erro.observe(viewLifecycleOwner) { mensagemErro ->

            if (mensagemErro != null) {

                Toast.makeText(context, mensagemErro, Toast.LENGTH_LONG).show();
                binding.txtDiagnosticoPrincipal.text = getString(R.string.label_aguardando_dados);

            }
        }
    }

    private fun popularDadosMedicos(resultado: ResultadoConsultaResponse) {

        binding.txtDiagnosticoPrincipal.text = resultado.diagnosticoPrincipal;
        binding.txtObservacoesVet.text = resultado.observacoesVeterinario
            ?: getString(R.string.label_nenhuma_prescricao);

        binding.containerItemsPrescricao.removeAllViews();

        if (resultado.prescricoes.isNullOrEmpty()) {

            binding.txtStatusPrescricoes.isVisible = true;
            binding.txtStatusPrescricoes.text = getString(R.string.label_nenhuma_prescricao);
            binding.containerItemsPrescricao.isVisible = false;

        } else {

            binding.txtStatusPrescricoes.isVisible = false;
            binding.containerItemsPrescricao.isVisible = true;

            resultado.prescricoes.forEach { prescricao ->

                val itemView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_prescricao, binding.containerItemsPrescricao, false);

                val txtNome = itemView.findViewById<TextView>(R.id.txt_nome_medicamento);
                val txtInstrucoes = itemView.findViewById<TextView>(R.id.txt_dosagem_instrucoes);

                txtNome.text = prescricao.nomeMedicamento;
                txtInstrucoes.text = "${prescricao.dosagem} | ${prescricao.instrucoesUso}";

                binding.containerItemsPrescricao.addView(itemView);

            }
        }
    }

    private fun setupListeners() {
        binding.btnBaixarPdf.setOnClickListener {
            Toast.makeText(context, getString(R.string.msg_pdf_em_breve), Toast.LENGTH_SHORT).show();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}