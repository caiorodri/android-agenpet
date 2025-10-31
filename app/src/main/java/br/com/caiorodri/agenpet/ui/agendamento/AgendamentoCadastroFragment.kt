package br.com.caiorodri.agenpet.ui.agendamento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import br.com.caiorodri.agenpet.model.agendamento.Status
import br.com.caiorodri.agenpet.model.animal.AnimalCadastroComplementar
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.model.usuario.UsuarioCadastroComplementar
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest

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
    private var listaStatus: List<Status> = emptyList()
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
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_editar_agendamento)
            binding.buttonSalvar.text = getString(R.string.button_atualizar)

            binding.autoCompleteTipo.setText(agendamento.tipo.nome, false)
            binding.autoCompleteAnimal.setText(agendamento.animal.nome, false)
            binding.autoCompleteVeterinario.setText(agendamento.veterinario.nome, false)
            binding.editTextDescricao.setText(agendamento.descricao)

            dataSelecionadaTimestamp = agendamento.dataAgendamentoInicio
            binding.editTextData.setText(formatadorDeData.format(Date(agendamento.dataAgendamentoInicio)))
            binding.autoCompleteHorario.setText(formatadorDeHora.format(Date(agendamento.dataAgendamentoInicio)))

            popularHorarios(agendamento.dataAgendamentoInicio)
            binding.menuHorario.isEnabled = true

            binding.inputLayoutData.isEnabled = false
            binding.menuHorario.isEnabled = false

            binding.menuStatus.visibility = View.VISIBLE
            binding.autoCompleteStatus.setText(agendamento.status.nome, false)

            if (agendamento.status.id == 2 || agendamento.status.id == 3) {
                desabilitarFormularioCompleto()
            }

        } else {
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.cadastrar_novo_agendamento)
            binding.buttonSalvar.text = getString(R.string.button_salvar)

            binding.menuStatus.visibility = View.GONE
        }


    }

    private fun desabilitarFormularioCompleto() {
        binding.menuTipoAgendamento.isEnabled = false
        binding.menuAnimal.isEnabled = false
        binding.menuVeterinario.isEnabled = false
        binding.menuStatus.isEnabled = false
        binding.inputLayoutData.isEnabled = false
        binding.menuHorario.isEnabled = false
        binding.inputLayoutDescricao.isEnabled = false
        binding.buttonSalvar.visibility = View.GONE
    }

    private fun setupListeners() {

        if(agendamentoParaEdicao == null){

            binding.inputLayoutData.setEndIconOnClickListener { mostrarDatePicker() }
            binding.editTextData.setOnClickListener { mostrarDatePicker() }

        }

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

        viewModel.status.observe(viewLifecycleOwner) { statusList ->
            if (agendamentoParaEdicao != null) {
                if (agendamentoParaEdicao?.status?.id == 1) {
                    listaStatus = statusList.filter { it.id == 2 || it.id == 1 }
                } else {
                    listaStatus = emptyList()
                }

            } else {
                listaStatus = statusList
            }

            val nomesStatus = listaStatus.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesStatus)
            binding.autoCompleteStatus.setAdapter(adapter)

            if (agendamentoParaEdicao != null) {
                binding.autoCompleteStatus.setText(agendamentoParaEdicao!!.status.nome, false)
            }
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

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(Calendar.YEAR, 2)
        val maxDateTimestamp = cal.timeInMillis

        constraintsBuilder.setEnd(maxDateTimestamp)

        constraintsBuilder.setStart(MaterialDatePicker.todayInUtcMilliseconds())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { dataTimestampUtc ->
            dataSelecionadaTimestamp = dataTimestampUtc
            binding.editTextData.setText(formatadorDeData.format(Date(dataTimestampUtc)))

            popularHorarios(dataTimestampUtc)
            binding.autoCompleteHorario.setText("", false)
            binding.menuHorario.isEnabled = true
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun popularHorarios(selectedTimestamp: Long) {
        val horarios = mutableListOf<String>()
        val cal = Calendar.getInstance()

        val calUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calUtc.timeInMillis = selectedTimestamp
        calUtc.set(Calendar.HOUR_OF_DAY, 0)
        calUtc.set(Calendar.MINUTE, 0)
        calUtc.set(Calendar.SECOND, 0)
        calUtc.set(Calendar.MILLISECOND, 0)
        val diaSelecionadoMeiaNoiteUtc = calUtc.timeInMillis

        val hojeMeiaNoiteUtc = MaterialDatePicker.todayInUtcMilliseconds()

        val isHoje = (diaSelecionadoMeiaNoiteUtc == hojeMeiaNoiteUtc)

        val horaAtualLocal = if (isHoje) {
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        } else {
            -1
        }

        for (hora in 8..21) {

            if (isHoje && hora <= horaAtualLocal) {
                continue
            }

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

        val cliente: Usuario? = sharedViewModel.usuarioLogado.value
        val animalSelecionado = listaAnimais.find { it.nome == binding.autoCompleteAnimal.text.toString() }
        val vetSelecionado = listaVeterinarios.find { it.nome == binding.autoCompleteVeterinario.text.toString() }
        val tipoSelecionado = listaTipos.find { it.nome == binding.autoCompleteTipo.text.toString() }

        val statusSelecionado = if (agendamentoParaEdicao != null) {
            listaStatus.find { it.nome == binding.autoCompleteStatus.text.toString() }
                ?: agendamentoParaEdicao!!.status
        } else {
            viewModel.status.value?.find { it.id == 1 }
        }

        val recepcionistaAutoAtendimento = viewModel.recepcionistaAutoAtendimento.value

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

        if (statusSelecionado == null) {
            Toast.makeText(context, "Erro: Status 'Aberto' (ID 1) não foi encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        val (hora, minuto) = horaString.split(":").map { it.toInt() }

        val calUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calUtc.timeInMillis = dataTimestamp
        val ano = calUtc.get(Calendar.YEAR)
        val mes = calUtc.get(Calendar.MONTH)
        val dia = calUtc.get(Calendar.DAY_OF_MONTH)

        val calendarInicio = Calendar.getInstance()
        calendarInicio.set(ano, mes, dia, hora, minuto, 0)
        calendarInicio.set(Calendar.MILLISECOND, 0)
        val dataInicio = calendarInicio.time

        val calendarFim = Calendar.getInstance()
        calendarFim.time = dataInicio
        calendarFim.add(Calendar.HOUR_OF_DAY, 1)
        val dataFim = calendarFim.time


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
                nome = tipoSelecionado.nome),
            dataAgendamentoInicio = dataInicio,
            dataAgendamentoFinal = dataFim,
            descricao = binding.editTextDescricao.text.toString().trim(),
            status = Status(id = statusSelecionado.id, nome = statusSelecionado.nome)
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