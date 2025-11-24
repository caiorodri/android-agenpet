package br.com.caiorodri.agenpet.ui.agendamento;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentAgendamentoCadastroBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.Status
import br.com.caiorodri.agenpet.model.agendamento.Tipo
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.animal.AnimalCadastroComplementar
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.model.usuario.UsuarioCadastroComplementar
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.ui.home.ClienteHomeActivity
import br.com.caiorodri.agenpet.ui.home.ClienteHomeSharedViewModel
import br.com.caiorodri.agenpet.ui.usuario.FuncionarioViewModel
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AgendamentoCadastroFragment : Fragment() {

    private var _binding: FragmentAgendamentoCadastroBinding? = null;
    private val binding get() = _binding!!;

    private val viewModel: AgendamentoCadastroViewModel by viewModels();
    private val sharedViewModel: ClienteHomeSharedViewModel by activityViewModels();

    private val formatadorDeDataUI = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };

    private val formatadorDeDataAPI = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };

    private val formatadorDeHora = SimpleDateFormat("HH:mm", Locale.getDefault());

    private var listaAnimais: List<Animal> = emptyList();
    private var listaVeterinarios: List<UsuarioResponse> = emptyList();
    private var listaTipos: List<Tipo> = emptyList();
    private var listaStatus: List<Status> = emptyList();
    private val args: AgendamentoCadastroFragmentArgs by navArgs();

    private var agendamentoParaEdicao: Agendamento? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendamentoCadastroBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        agendamentoParaEdicao = args.agendamento;

        setupUIBase(agendamentoParaEdicao);
        setupListeners();
        setupObservers();
    }

    private fun setupUIBase(agendamento: Agendamento?) {

        if (agendamento != null) {

            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_editar_agendamento);
            binding.buttonSalvar.text = getString(R.string.button_atualizar);

            binding.autoCompleteTipo.setText(agendamento.tipo.getNomeTraduzido(requireContext()), false);
            binding.autoCompleteAnimal.setText(agendamento.animal.nome, false);
            binding.autoCompleteVeterinario.setText(agendamento.veterinario.nome, false);
            binding.editTextDescricao.setText(agendamento.descricao);

            viewModel.setDataSelecionada(agendamento.dataAgendamentoInicio, formatadorDeDataAPI.format(Date(agendamento.dataAgendamentoInicio)));
            viewModel.setHoraSelecionada(formatadorDeHora.format(Date(agendamento.dataAgendamentoInicio)));

            binding.editTextData.setText(formatadorDeDataUI.format(Date(agendamento.dataAgendamentoInicio)));
            binding.autoCompleteHorario.setText(formatadorDeHora.format(Date(agendamento.dataAgendamentoInicio)));

            binding.menuHorario.isEnabled = true;

            binding.inputLayoutData.isEnabled = false;
            binding.menuHorario.isEnabled = false;
            binding.menuTipoAgendamento.isEnabled = false;
            binding.menuAnimal.isEnabled = false;
            binding.menuVeterinario.isEnabled = false;

            binding.menuStatus.visibility = View.VISIBLE;
            binding.autoCompleteStatus.setText(agendamento.status.getNomeTraduzido(requireContext()), false);

            if (agendamento.status.id == 2 || agendamento.status.id == 3) {
                desabilitarFormularioCompleto();
            }

        } else {

            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.cadastrar_novo_agendamento);
            binding.buttonSalvar.text = getString(R.string.button_salvar);

            binding.menuStatus.visibility = View.GONE;

            binding.autoCompleteHorario.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyList<String>())
            );

            binding.menuVeterinario.isEnabled = (viewModel.tipoSelecionadoId.value != null);
            binding.inputLayoutData.isEnabled = (viewModel.veterinarioSelecionadoId.value != null);
            binding.menuHorario.isEnabled = (viewModel.dataSelecionadaApi.value != null);

            viewModel.dataSelecionadaTimestamp.value?.let {
                binding.editTextData.setText(formatadorDeDataUI.format(Date(it)));
            }

        }

    }

    private fun desabilitarFormularioCompleto() {
        binding.menuTipoAgendamento.isEnabled = false;
        binding.menuAnimal.isEnabled = false;
        binding.menuVeterinario.isEnabled = false;
        binding.menuStatus.isEnabled = false;
        binding.inputLayoutData.isEnabled = false;
        binding.menuHorario.isEnabled = false;
        binding.inputLayoutDescricao.isEnabled = false;
        binding.buttonSalvar.visibility = View.GONE;
    }

    private fun setupListeners() {

        if(agendamentoParaEdicao == null){

            binding.autoCompleteTipo.setOnItemClickListener { parent, _, position, _ ->
                val nomeTraduzido = parent.getItemAtPosition(position) as String;
                val tipo = listaTipos.find { it.getNomeTraduzido(requireContext()) == nomeTraduzido };

                viewModel.setTipoSelecionado(tipo);

                binding.autoCompleteVeterinario.setText("", false);
                binding.editTextData.setText("");
                binding.autoCompleteHorario.setText("", false);

                binding.menuVeterinario.isEnabled = (tipo != null);
                binding.inputLayoutData.isEnabled = false;
                binding.menuHorario.isEnabled = false;

                buscarHorariosDisponiveisSePronto();
            };

            binding.autoCompleteVeterinario.setOnItemClickListener { parent, _, position, _ ->

                val nomeVet = parent.getItemAtPosition(position) as String;
                val vet = listaVeterinarios.find { it.nome == nomeVet };

                viewModel.setVeterinarioSelecionado(vet);

                binding.editTextData.setText("");
                binding.autoCompleteHorario.setText("", false);
                binding.menuHorario.isEnabled = false;

                binding.inputLayoutData.isEnabled = (vet != null);

                buscarHorariosDisponiveisSePronto();
            };

            binding.inputLayoutData.setEndIconOnClickListener { mostrarDatePicker(); };
            binding.editTextData.setOnClickListener { mostrarDatePicker(); };

        }

        binding.autoCompleteHorario.setOnItemClickListener { parent, _, position, _ ->
            val hora = parent.getItemAtPosition(position) as String;
            viewModel.setHoraSelecionada(hora);
        };

        binding.buttonSalvar.setOnClickListener {

            if (agendamentoParaEdicao != null) {

                val cancelarAgendamento: Status? = listaStatus.find { it.getNomeTraduzido(requireContext()) == binding.autoCompleteStatus.text.toString()}

                if(cancelarAgendamento?.id == 2){

                    mostrarDialogoCancelamento();
                    return@setOnClickListener;
                }

            }

            validarEsalvar();

        };

    }

    private fun setupObservers() {

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.savingOverlay.isVisible = isLoading;
            binding.buttonSalvar.isEnabled = !isLoading;
        };

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                mostrarDialogoErro(error);
                viewModel.resetError();
            }
        };

        viewModel.agendamentoSalvo.observe(viewLifecycleOwner) { agendamentoSalvo ->
            agendamentoSalvo ?: return@observe;

            Toast.makeText(requireContext(), getString(R.string.sucesso_agendamento_salvo), Toast.LENGTH_SHORT).show();

            val agendamentoConvertido = Agendamento(agendamentoSalvo);
            val agendamentoFinal = agendamentoConvertido.copy();

            sharedViewModel.atualizarAgendamentoLocalmente(agendamentoFinal);
            (activity as? ClienteHomeActivity)?.carregarDadosDoUsuario();

            viewModel.resetAgendamentoSalvo();
            findNavController().popBackStack();

        };

        viewModel.status.observe(viewLifecycleOwner) { statusList ->
            if (agendamentoParaEdicao != null) {
                if (agendamentoParaEdicao?.status?.id == 1) {
                    listaStatus = statusList.filter { it.id == 2 || it.id == 1 };
                } else {
                    listaStatus = emptyList();
                }

            } else {
                listaStatus = statusList;
            }

            val nomesStatus = listaStatus.map { it.getNomeTraduzido(requireContext()) }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesStatus);
            binding.autoCompleteStatus.setAdapter(adapter);

            if (agendamentoParaEdicao != null) {
                binding.autoCompleteStatus.setText(agendamentoParaEdicao!!.status.getNomeTraduzido(requireContext()), false);
            }
        };


        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->
            if (usuario?.animais != null) {
                listaAnimais = usuario.animais!!;
                val nomesAnimais = listaAnimais.map { it.nome };
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesAnimais);
                binding.autoCompleteAnimal.setAdapter(adapter);
            }
        };

        viewModel.tipos.observe(viewLifecycleOwner) { tipos ->

            listaTipos = tipos;
            val nomesTipos = tipos.map { it.getNomeTraduzido(requireContext()) };
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesTipos);
            binding.autoCompleteTipo.setAdapter(adapter);

            if (agendamentoParaEdicao == null && viewModel.tipoSelecionadoId.value != null) {

                val tipoSalvo = tipos.find { it.id == viewModel.tipoSelecionadoId.value };

                if (tipoSalvo != null) {
                    binding.autoCompleteTipo.setText(tipoSalvo.getNomeTraduzido(requireContext()), false);
                }

            } else if (agendamentoParaEdicao != null) {

                binding.autoCompleteTipo.setText(agendamentoParaEdicao!!.tipo.getNomeTraduzido(requireContext()), false);

            }

        }

        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->

            listaVeterinarios = veterinarios;

            val nomesVets = veterinarios.map { it.nome };
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesVets);

            binding.autoCompleteVeterinario.setAdapter(adapter);

            if (agendamentoParaEdicao == null && viewModel.veterinarioSelecionadoId.value != null) {

                val vetSalvo = veterinarios.find { it.id == viewModel.veterinarioSelecionadoId.value };

                if (vetSalvo != null) {

                    binding.autoCompleteVeterinario.setText(vetSalvo.nome, false);

                }

            }

        }

        viewModel.isLoadingHorarios.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarHorarios.isVisible = isLoading;


            if (isLoading) {
                binding.menuHorario.isEnabled = false;
            } else {
                val temHorarios = viewModel.horariosDisponiveis.value?.isNotEmpty() ?: false
                binding.menuHorario.isEnabled = temHorarios
            }
        }

        viewModel.horariosDisponiveis.observe(viewLifecycleOwner) { horarios ->

            val novoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, horarios);
            binding.autoCompleteHorario.setAdapter(novoAdapter);

            val temHorarios = horarios.isNotEmpty();
            binding.menuHorario.isEnabled = temHorarios && (viewModel.isLoadingHorarios.value == false);

            if (!temHorarios && viewModel.dataSelecionadaApi.value != null && viewModel.veterinarioSelecionadoId.value != null && viewModel.tipoSelecionadoId.value != null) {
                binding.menuHorario.error = getString(R.string.erro_sem_horarios_disponiveis);
            } else {
                binding.menuHorario.error = null;
            }

            viewModel.horaSelecionada.value?.let { horaSalva ->
                if (horarios.contains(horaSalva)) {
                    binding.autoCompleteHorario.setText(horaSalva, false);
                }
            }

        }
    }

    private fun mostrarDatePicker() {

        val constraintsBuilder = CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.YEAR, 2);
        val maxDateTimestamp = cal.timeInMillis;

        constraintsBuilder.setEnd(maxDateTimestamp);
        constraintsBuilder.setStart(MaterialDatePicker.todayInUtcMilliseconds());

        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.titulo_datepicker_data_agendamento))
            .setCalendarConstraints(constraintsBuilder.build());

        viewModel.dataSelecionadaTimestamp.value?.let {
            datePickerBuilder.setSelection(it);
        }

        val datePicker = datePickerBuilder.build();

        datePicker.addOnPositiveButtonClickListener { dataTimestampUtc ->

            val dataApi = formatadorDeDataAPI.format(Date(dataTimestampUtc));

            viewModel.setDataSelecionada(dataTimestampUtc, dataApi);

            binding.editTextData.setText(formatadorDeDataUI.format(Date(dataTimestampUtc)));
            
            binding.autoCompleteHorario.setText("", false);
            binding.menuHorario.error = null;

            buscarHorariosDisponiveisSePronto();
        };

        datePicker.show(childFragmentManager, "DATE_PICKER");
    }

    private fun buscarHorariosDisponiveisSePronto() {

        val tipoId = viewModel.tipoSelecionadoId.value;
        val vetId = viewModel.veterinarioSelecionadoId.value;
        val dataApi = viewModel.dataSelecionadaApi.value;

        if (tipoId != null && vetId != null && dataApi != null) {

            Log.d("AgendamentoCadastro", "Buscando horários para Tipo ID: $tipoId, Vet ID: $vetId, Data: $dataApi");
            viewModel.buscarHorariosDisponiveis(vetId, dataApi, tipoId);

        }
    }

    private fun mostrarDialogoCancelamento(){

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_cancelar_agendamento))
            .setMessage(getString(R.string.dialog_message_cancelar_agendamento))
            .setPositiveButton(getString(R.string.dialog_button_cancelar_agendamento)) { dialog, _ ->
                validarEsalvar();
                dialog.dismiss();
            }
            .setNegativeButton(getString(R.string.dialog_button_voltar)) { dialog, _ ->
                dialog.dismiss();
            }
            .show();

    }

    private fun validarEsalvar() {
        if (!validarCampos()) {
            Toast.makeText(requireContext(), getString(R.string.toast_preencher_campos_obrigatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        val cliente: Usuario? = sharedViewModel.usuarioLogado.value;
        val animalSelecionado = listaAnimais.find { it.nome == binding.autoCompleteAnimal.text.toString() };

        val vetSelecionado = if (agendamentoParaEdicao != null) {

            listaVeterinarios.find { it.nome == binding.autoCompleteVeterinario.text.toString() };

        } else {

            listaVeterinarios.find { it.id == viewModel.veterinarioSelecionadoId.value };

        }

        val nomeTipoTraduzido = binding.autoCompleteTipo.text.toString();
        val tipoSelecionado = listaTipos.find { it.getNomeTraduzido(requireContext()) == nomeTipoTraduzido };

        val nomeStatusTraduzido = binding.autoCompleteStatus.text.toString();

        val statusSelecionado = if (agendamentoParaEdicao != null) {

            listaStatus.find { it.getNomeTraduzido(requireContext()) == nomeStatusTraduzido } ?: agendamentoParaEdicao!!.status;

        } else {

            viewModel.status.value?.find { it.id == 1 };

        }

        val recepcionistaAutoAtendimento = viewModel.recepcionistaAutoAtendimento.value;

        val dataTimestamp = viewModel.dataSelecionadaTimestamp.value;
        val horaString = binding.autoCompleteHorario.text.toString();

        if (cliente?.id == null) {
            Toast.makeText(requireContext(), getString(R.string.toast_erro_usuario_nao_encontrado), Toast.LENGTH_SHORT).show();
            return;
        }
        if (animalSelecionado?.id == null) {
            val erroMsg = getString(R.string.erro_animal_invalido);
            Toast.makeText(requireContext(), erroMsg, Toast.LENGTH_SHORT).show();
            binding.menuAnimal.error = erroMsg;
            return;
        }
        if (vetSelecionado?.id == null) {
            val erroMsg = getString(R.string.erro_veterinario_invalido);
            Toast.makeText(requireContext(), erroMsg, Toast.LENGTH_SHORT).show();
            binding.menuVeterinario.error = erroMsg;
            return;
        }

        if (tipoSelecionado == null) {
            val erroMsg = getString(R.string.erro_tipo_invalido);
            Toast.makeText(requireContext(), erroMsg, Toast.LENGTH_SHORT).show();
            binding.menuTipoAgendamento.error = erroMsg;
            return;
        }
        if (dataTimestamp == null) {
            val erroMsg = getString(R.string.erro_data_invalida);
            Toast.makeText(requireContext(), erroMsg, Toast.LENGTH_SHORT).show();
            binding.inputLayoutData.error = getString(R.string.erro_obrigatorio);
            return;
        }
        if (statusSelecionado == null) {
            Toast.makeText(requireContext(), getString(R.string.erro_status_nao_encontrado), Toast.LENGTH_SHORT).show();
            return;
        }

        val (hora, minuto) = horaString.split(":").map { it.toInt() };

        val calUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calUtc.timeInMillis = dataTimestamp;
        val ano = calUtc.get(Calendar.YEAR);
        val mes = calUtc.get(Calendar.MONTH);
        val dia = calUtc.get(Calendar.DAY_OF_MONTH);

        val calendarInicio = Calendar.getInstance();
        calendarInicio.set(ano, mes, dia, hora, minuto, 0);
        calendarInicio.set(Calendar.MILLISECOND, 0);
        val dataInicio = calendarInicio.time;

        val calendarFim = Calendar.getInstance();
        calendarFim.time = dataInicio;
        calendarFim.add(Calendar.MINUTE, tipoSelecionado.duracaoMinutos);
        val dataFim = calendarFim.time;


        val request = AgendamentoRequest(
            id = agendamentoParaEdicao?.id,
            cliente = UsuarioCadastroComplementar(
                id = cliente.id,
                nome = cliente.nome,
                email = cliente.email),
            recepcionista = UsuarioCadastroComplementar(
                id = recepcionistaAutoAtendimento!!.id,
                nome = recepcionistaAutoAtendimento.nome,
                email = recepcionistaAutoAtendimento.email
            ),
            animal = AnimalCadastroComplementar(
                id = animalSelecionado.id,
                nome = animalSelecionado.nome),
            veterinario = UsuarioCadastroComplementar(
                id = vetSelecionado.id,
                nome = vetSelecionado.nome,
                email = vetSelecionado.email),
            tipo = Tipo(
                id = tipoSelecionado.id,
                nome = tipoSelecionado.nome,
                duracaoMinutos = tipoSelecionado.duracaoMinutos),
            dataAgendamentoInicio = dataInicio,
            dataAgendamentoFinal = dataFim,
            descricao = binding.editTextDescricao.text.toString().trim(),
            status = Status(id = statusSelecionado.id, nome = statusSelecionado.nome)
        );

        viewModel.salvarOuAtualizarAgendamento(request);
    }

    private fun validarCampos(): Boolean {

        var valido = true;

        binding.menuTipoAgendamento.error = null;
        binding.menuAnimal.error = null;
        binding.menuVeterinario.error = null;
        binding.inputLayoutData.error = null;
        binding.menuHorario.error = null;

        val erroObrigatorio = getString(R.string.erro_obrigatorio);

        if (binding.autoCompleteTipo.text.isNullOrBlank()) {
            binding.menuTipoAgendamento.error = erroObrigatorio;
            valido = false;
        }
        if (binding.autoCompleteAnimal.text.isNullOrBlank()) {
            binding.menuAnimal.error = erroObrigatorio;
            valido = false;
        }
        if (binding.autoCompleteVeterinario.text.isNullOrBlank()) {
            binding.menuVeterinario.error = erroObrigatorio;
            valido = false;
        }
        if (binding.editTextData.text.isNullOrBlank()) {
            binding.inputLayoutData.error = erroObrigatorio;
            valido = false;
        }
        if (binding.autoCompleteHorario.text.isNullOrBlank()) {
            binding.menuHorario.error = erroObrigatorio;
            valido = false;
        }
        return valido;
    }

    private fun mostrarDialogoErro(mensagem: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_erro_agendamento))
            .setMessage(mensagem)
            .setPositiveButton(getString(R.string.dialog_button_ok)) { dialog, _ ->
                dialog.dismiss();
            }
            .show();
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}