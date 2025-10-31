package br.com.caiorodri.agenpet.ui.agendamento

import android.os.Bundle
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
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentAgendamentoCadastroBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.agendamento.AgendamentoRequest
import br.com.caiorodri.agenpet.model.agendamento.Tipo
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.ui.home.HomeActivity
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.navigation.fragment.navArgs

class AgendamentoCadastroFragment : Fragment() {

    private var _binding: FragmentAgendamentoCadastroBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AgendamentoCadastroViewModel by viewModels()
    private val sharedViewModel: HomeSharedViewModel by activityViewModels()

    private val formatadorDeData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val formatadorDeHora = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var listaAnimais: List<Animal> = emptyList()
    private var listaVeterinarios: List<UsuarioResponse> = emptyList()
    private var listaTipos: List<Tipo> = emptyList()
    private lateinit var horarioAdapter: ArrayAdapter<String>

    private var dataSelecionadaTimestamp: Long? = null

    private val args: AgendamentoCadastroFragmentArgs by navArgs()

    private var agendamentoParaEdicao: Agendamento? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendamentoCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        agendamentoParaEdicao = args.agendamento

        setupUIBase(agendamentoParaEdicao)
        setupListeners()
        setupObservers()
    }

    private fun setupUIBase(agendamento: Agendamento?) {

        horarioAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>())
        binding.autoCompleteHorario.setAdapter(horarioAdapter)

        if (agendamento != null) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Editar Agendamento"
            binding.buttonSalvar.text = getString(R.string.button_atualizar)

            binding.autoCompleteTipo.setText(agendamento.tipo.nome, false)
            binding.autoCompleteAnimal.setText(agendamento.animal.nome, false)
            binding.autoCompleteVeterinario.setText(agendamento.veterinario.nome, false)
            binding.editTextDescricao.setText(agendamento.descricao)

            dataSelecionadaTimestamp = agendamento.dataAgendamentoInicio
            binding.editTextData.setText(formatadorDeData.format(Date(agendamento.dataAgendamentoInicio)))
            binding.autoCompleteHorario.setText(formatadorDeHora.format(Date(agendamento.dataAgendamentoInicio)))

            popularHorarios()
            binding.menuHorario.isEnabled = true

        } else {
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Novo Agendamento"
            binding.buttonSalvar.text = getString(R.string.button_salvar)
        }


    }

    private fun setupListeners() {
        binding.inputLayoutData.setEndIconOnClickListener { mostrarDatePicker() }
        binding.editTextData.setOnClickListener { mostrarDatePicker() }

        binding.buttonSalvar.setOnClickListener {
            validarEsalvar()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.savingOverlay.isVisible = isLoading
            binding.buttonSalvar.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                viewModel.resetError()
            }
        }

        viewModel.agendamentoSalvo.observe(viewLifecycleOwner) { agendamentoSalvo ->
            agendamentoSalvo ?: return@observe

            Toast.makeText(context, "Agendamento salvo com sucesso!", Toast.LENGTH_SHORT).show()
            (activity as? HomeActivity)?.carregarDadosDoUsuario()
            viewModel.resetAgendamentoSalvo()
            findNavController().popBackStack()
        }


        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->
            if (usuario?.animais != null) {
                listaAnimais = usuario.animais!!
                val nomesAnimais = listaAnimais.map { it.nome }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesAnimais)
                binding.autoCompleteAnimal.setAdapter(adapter)
            }
        }

        viewModel.tipos.observe(viewLifecycleOwner) { tipos ->
            listaTipos = tipos
            val nomesTipos = tipos.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesTipos)
            binding.autoCompleteTipo.setAdapter(adapter)
        }

        viewModel.veterinarios.observe(viewLifecycleOwner) { veterinarios ->
            listaVeterinarios = veterinarios
            val nomesVets = veterinarios.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesVets)
            binding.autoCompleteVeterinario.setAdapter(adapter)
        }
    }

    private fun mostrarDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { dataTimestampUtc ->
            dataSelecionadaTimestamp = dataTimestampUtc
            binding.editTextData.setText(formatadorDeData.format(Date(dataTimestampUtc)))

            popularHorarios()
            binding.autoCompleteHorario.setText("", false)
            binding.menuHorario.isEnabled = true
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun popularHorarios() {
        val horarios = mutableListOf<String>()
        val cal = Calendar.getInstance()

        for (hora in 9..17) {
            cal.set(Calendar.HOUR_OF_DAY, hora)
            cal.set(Calendar.MINUTE, 0)
            horarios.add(formatadorDeHora.format(cal.time))
        }

        horarioAdapter.clear()
        horarioAdapter.addAll(horarios)
        horarioAdapter.notifyDataSetChanged()
    }

    private fun validarEsalvar() {
        if (!validarCampos()) {
            Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = sharedViewModel.usuarioLogado.value
        val animalSelecionado = listaAnimais.find { it.nome == binding.autoCompleteAnimal.text.toString() }
        val vetSelecionado = listaVeterinarios.find { it.nome == binding.autoCompleteVeterinario.text.toString() }
        val tipoSelecionado = listaTipos.find { it.nome == binding.autoCompleteTipo.text.toString() }

        val dataTimestamp = dataSelecionadaTimestamp
        val horaString = binding.autoCompleteHorario.text.toString()

        if (cliente?.id == null) {
            Toast.makeText(context, "Erro: Cliente não carregado.", Toast.LENGTH_SHORT).show()
            return
        }
        if (animalSelecionado?.id == null) {
            Toast.makeText(context, "Erro: Animal inválido ou não selecionado.", Toast.LENGTH_SHORT).show()
            binding.menuAnimal.error = "Selecione um pet válido"
            return
        }
        if (vetSelecionado?.id == null) {
            Toast.makeText(context, "Erro: Veterinário inválido ou não selecionado.", Toast.LENGTH_SHORT).show()
            binding.menuVeterinario.error = "Selecione um veterinário válido"
            return
        }
        if (tipoSelecionado == null) {
            Toast.makeText(context, "Erro: Tipo inválido ou não selecionado.", Toast.LENGTH_SHORT).show()
            binding.menuTipoAgendamento.error = "Selecione um tipo válido"
            return
        }
        if (dataTimestamp == null) {
            Toast.makeText(context, "Erro: Data inválida.", Toast.LENGTH_SHORT).show()
            binding.inputLayoutData.error = "Obrigatório"
            return
        }

        val (hora, minuto) = horaString.split(":").map { it.toInt() }

        val calendarInicio = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarInicio.timeInMillis = dataTimestamp
        calendarInicio.set(Calendar.HOUR_OF_DAY, hora)
        calendarInicio.set(Calendar.MINUTE, minuto)
        calendarInicio.set(Calendar.SECOND, 0)
        calendarInicio.set(Calendar.MILLISECOND, 0)
        val dataInicio = calendarInicio.time

        val calendarFim = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarFim.time = dataInicio
        calendarFim.add(Calendar.HOUR_OF_DAY, 1)
        val dataFim = calendarFim.time

        val request = AgendamentoRequest(
            id = agendamentoParaEdicao?.id,
            idCliente = cliente.id,
            idAnimal = animalSelecionado.id,
            idVeterinario = vetSelecionado.id,
            idTipo = tipoSelecionado.id,
            idStatus = agendamentoParaEdicao?.status?.id ?: 1,
            dataAgendamentoInicio = dataInicio,
            dataAgendamentoFinal = dataFim,
            descricao = binding.editTextDescricao.text.toString().trim()
        )

        viewModel.salvarOuAtualizarAgendamento(request)
    }

    private fun validarCampos(): Boolean {
        var valido = true
        binding.menuTipoAgendamento.error = null
        binding.menuAnimal.error = null
        binding.menuVeterinario.error = null
        binding.inputLayoutData.error = null
        binding.menuHorario.error = null

        if (binding.autoCompleteTipo.text.isNullOrBlank()) {
            binding.menuTipoAgendamento.error = "Obrigatório"
            valido = false
        }
        if (binding.autoCompleteAnimal.text.isNullOrBlank()) {
            binding.menuAnimal.error = "Obrigatório"
            valido = false
        }
        if (binding.autoCompleteVeterinario.text.isNullOrBlank()) {
            binding.menuVeterinario.error = "Obrigatório"
            valido = false
        }
        if (binding.editTextData.text.isNullOrBlank()) {
            binding.inputLayoutData.error = "Obrigatório"
            valido = false
        }
        if (binding.autoCompleteHorario.text.isNullOrBlank()) {
            binding.menuHorario.error = "Obrigatório"
            valido = false
        }
        return valido
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}