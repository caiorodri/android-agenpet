package br.com.caiorodri.agenpet.ui.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.activityViewModels;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager;
import br.com.caiorodri.agenpet.databinding.FragmentHorarioBinding;
import br.com.caiorodri.agenpet.model.enums.DiaSemanaEnum;
import br.com.caiorodri.agenpet.model.usuario.VeterinarioHorario;
import br.com.caiorodri.agenpet.ui.adapter.HorarioAdapter;
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel;
import br.com.caiorodri.agenpet.ui.usuario.HorarioViewModel
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

class HorarioFragment : Fragment() {

    private val args: HorarioFragmentArgs by navArgs();
    private var idVeterinarioAlvo: Long? = null;
    private var _binding: FragmentHorarioBinding? = null;
    private val binding get() = _binding!!;
    private val viewModel: HorarioViewModel by viewModels();
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();
    private lateinit var adapter: HorarioAdapter;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHorarioBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState);

        definirVeterinarioAlvo();

        if (idVeterinarioAlvo == null) {
            Toast.makeText(context, getString(R.string.erro_usuario_nao_identificado), Toast.LENGTH_SHORT).show();
            return;
        }

        setupUI();
        setupObservers();

        viewModel.carregarHorarios(idVeterinarioAlvo!!);

    }

    private fun definirVeterinarioAlvo() {

        val veterinarioArgumento = args.veterinario;
        val usuarioLogado = sharedViewModel.usuarioLogado.value;

        if (veterinarioArgumento != null) {

            idVeterinarioAlvo = veterinarioArgumento.id;

            binding.titleNovoHorario.text = getString(R.string.title_horarios_de, veterinarioArgumento.nome);
        } else {

            idVeterinarioAlvo = usuarioLogado?.id;
            binding.titleNovoHorario.text = getString(R.string.title_novo_horario);

        }
    }

    private fun setupUI() {

        adapter = HorarioAdapter { horario ->

            viewModel.deletarHorario(horario.id!!, idVeterinarioAlvo!!);

        };

        binding.recyclerHorarios.layoutManager = LinearLayoutManager(context);
        binding.recyclerHorarios.adapter = adapter;

        val dias = DiaSemanaEnum.entries.map { it.getNomeTraduzido(requireContext()) };
        val adapterDias = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, dias);

        binding.autoCompleteDia.setAdapter(adapterDias);

        binding.editHoraInicio.setOnClickListener { showTimePicker(true); }
        binding.editHoraFim.setOnClickListener { showTimePicker(false); }

        binding.btnAdicionar.setOnClickListener {

            validarESalvar();

        }

    }

    private fun showTimePicker(isInicio: Boolean) {

        val titulo = if (isInicio) getString(R.string.picker_titulo_inicio) else getString(R.string.picker_titulo_fim);

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(8)
            .setMinute(0)
            .setTitleText(titulo)
            .build();

        picker.addOnPositiveButtonClickListener {

            val horaFormatada = String.format("%02d:%02d", picker.hour, picker.minute);

            if (isInicio) {
                binding.editHoraInicio.setText(horaFormatada);
            } else {
                binding.editHoraFim.setText(horaFormatada);
            }

        }

        picker.show(parentFragmentManager, "TIME_PICKER");
    }

    private fun validarESalvar() {

        val diaNome = binding.autoCompleteDia.text.toString();
        val horaInicio = binding.editHoraInicio.text.toString();
        val horaFim = binding.editHoraFim.text.toString();

        if (diaNome.isBlank() || horaInicio.isBlank() || horaFim.isBlank()) {

            Toast.makeText(context, getString(R.string.toast_preencher_campos_obrigatorios), Toast.LENGTH_SHORT).show();
            return;

        }

        val diaEnum = DiaSemanaEnum.entries.find { it.getNomeTraduzido(requireContext()) == diaNome };

        if (diaEnum == null) {

            Toast.makeText(context, R.string.erro_dia_invalido, Toast.LENGTH_SHORT).show();
            return;

        }

        val novoHorario = VeterinarioHorario(
            id = null,
            idVeterinario = idVeterinarioAlvo,
            idDiaSemana = diaEnum.id,
            horaInicio = horaInicio,
            horaFim = horaFim
        );

        viewModel.salvarHorario(novoHorario);

    }

    private fun setupObservers() {

        viewModel.listaHorarios.observe(viewLifecycleOwner) { lista ->
            val novaLista = lista.sortedBy { it.idDiaSemana }
            adapter.submitList(novaLista);
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.loadingOverlay.isVisible = loading;
            binding.btnAdicionar.isEnabled = !loading;
        }

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro != null) {
                Toast.makeText(context, erro, Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}