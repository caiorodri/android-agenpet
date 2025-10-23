package br.com.caiorodri.agenpet.ui.animal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentAnimalCadastroBinding
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AnimalCadastroFragment : Fragment() {

    private var _binding: FragmentAnimalCadastroBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnimalCadastroViewModel by viewModels()
    private val sharedViewModel: HomeSharedViewModel by activityViewModels()
    private val args: AnimalCadastroFragmentArgs by navArgs()
    private var animalParaEdicao: Animal? = null
    private val formatadorDeData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimalCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animalParaEdicao = args.animal

        setupObservers()
        setupListeners()
        setupUI()
    }

    private fun setupUI() {
        val animal = animalParaEdicao
        if (animal != null) {
            // MODO EDIÇÃO
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Editar Pet"
            binding.textViewIdAnimal.text = "ID do Pet: ${animal.id}"
            binding.textViewIdAnimal.isVisible = true

            binding.editTextNome.setText(animal.nome)
            binding.editTextDescricao.setText(animal.descricao)

            animal.dataNascimento?.let { timestamp ->
                binding.editTextDataNascimento.setText(formatadorDeData.format(Date(timestamp)))
            }

            val especieDoAnimal = animal.raca?.especie
            binding.autoCompleteEspecie.setText(especieDoAnimal?.nome, false)

            viewModel.filtrarRacasPorEspecie(especieDoAnimal)
            binding.autoCompleteRaca.setText(animal.raca?.nome, false)
            binding.menuRaca.isEnabled = true

            if (animal.sexo?.nome == "Macho") binding.toggleButtonGroupSexo.check(R.id.button_macho)
            else binding.toggleButtonGroupSexo.check(R.id.button_femea)

            binding.editTextPeso.setText(animal.peso?.toString())
            binding.editTextAltura.setText(animal.altura?.toString())
            if (animal.castrado == true) binding.radioCastradoSim.isChecked = true
            else binding.radioCastradoNao.isChecked = true

            binding.inputLayoutPeso.isEnabled = false
            binding.inputLayoutAltura.isEnabled = false
            binding.radioGroupCastrado.children.forEach { it.isEnabled = false }

            binding.buttonSalvar.text = "Atualizar"
        } else {
            // MODO CADASTRO
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Novo Pet"
            binding.textViewIdAnimal.isVisible = false
            binding.labelInfoAdicional.isVisible = false
            binding.buttonSalvar.text = "Salvar"
        }
    }

    private fun setupListeners() {
        binding.inputLayoutDataNascimento.setEndIconOnClickListener { mostrarDatePicker() }
        binding.editTextDataNascimento.setOnClickListener { mostrarDatePicker() }

        binding.autoCompleteEspecie.setOnItemClickListener { parent, _, position, _ ->
            val nomeEspecie = parent.getItemAtPosition(position) as String
            val especieSelecionada = viewModel.especies.value?.find { it.nome == nomeEspecie }

            binding.autoCompleteRaca.text = null
            binding.menuRaca.isEnabled = (especieSelecionada != null)
            viewModel.filtrarRacasPorEspecie(especieSelecionada)
        }

        binding.buttonSalvar.setOnClickListener {
            salvarDadosDoAnimal()
        }
    }

    private fun setupObservers() {
        viewModel.especies.observe(viewLifecycleOwner) { especies ->
            val nomesDasEspecies = especies.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesDasEspecies)
            binding.autoCompleteEspecie.setAdapter(adapter)
        }

        viewModel.racasFiltradas.observe(viewLifecycleOwner) { racas ->
            val nomesDasRacas = racas.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesDasRacas)
            binding.autoCompleteRaca.setAdapter(adapter)
        }

        viewModel.eventoSalvo.observe(viewLifecycleOwner) { salvo ->
            if (salvo) {
                Toast.makeText(context, "Pet salvo com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun salvarDadosDoAnimal() {

        if (!validarCampos()) {
            Toast.makeText(context, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val dono = sharedViewModel.usuarioLogado.value
        if (dono == null) {
            Toast.makeText(context, "Erro: usuário não encontrado. Tente novamente.", Toast.LENGTH_LONG).show()
            return
        }

        val dataNascimentoString = binding.editTextDataNascimento.text.toString();

        val dataNascimentoTimestamp = if (dataNascimentoString.isNotBlank()) {
            try {
                formatadorDeData.parse(dataNascimentoString)?.time
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        val nome = binding.editTextNome.text.toString().trim()
        val descricao = binding.editTextDescricao.text.toString().trim()

        val nomeRaca = binding.autoCompleteRaca.text.toString()
        val raca = viewModel.racasFiltradas.value?.find { it.nome == nomeRaca }

        val nomeSexo = if (binding.toggleButtonGroupSexo.checkedButtonId == R.id.button_macho) "Macho" else "Fêmea"
        val sexo = viewModel.sexos.value?.find { it.nome == nomeSexo }

        val peso = if (animalParaEdicao != null) animalParaEdicao?.peso else binding.editTextPeso.text.toString().toDoubleOrNull()
        val altura = if (animalParaEdicao != null) animalParaEdicao?.altura else binding.editTextAltura.text.toString().toDoubleOrNull()
        val castrado = if (animalParaEdicao != null) animalParaEdicao?.castrado else binding.radioGroupCastrado.checkedRadioButtonId == R.id.radio_castrado_sim

        val animal = Animal(
            id = animalParaEdicao?.id,
            nome = nome,
            dono = dono,
            dataNascimento = dataNascimentoTimestamp,
            descricao = descricao,
            raca = raca,
            sexo = sexo,
            peso = peso,
            altura = altura,
            castrado = castrado,
            agendamentos = animalParaEdicao?.agendamentos
        )

        viewModel.salvarAnimal(animal)
    }

    private fun validarCampos(): Boolean {
        var camposValidos = true

        binding.inputLayoutNome.error = null
        binding.menuEspecie.error = null
        binding.menuRaca.error = null
        binding.inputLayoutPeso.error = null
        binding.inputLayoutAltura.error = null

        if (binding.editTextNome.text.isNullOrBlank()) {
            binding.inputLayoutNome.error = "Nome é obrigatório"
            camposValidos = false
        }

        if (binding.autoCompleteEspecie.text.isNullOrBlank()) {
            binding.menuEspecie.error = "Espécie é obrigatória"
            camposValidos = false
        }

        if (binding.autoCompleteRaca.text.isNullOrBlank()) {
            binding.menuRaca.error = "Raça é obrigatória"
            camposValidos = false
        }

        if (binding.toggleButtonGroupSexo.checkedButtonId == View.NO_ID) {
            Toast.makeText(context, "Selecione o sexo do pet", Toast.LENGTH_SHORT).show()
            camposValidos = false
        }

        if (animalParaEdicao == null) {
            if (binding.editTextPeso.text.isNullOrBlank()) {
                binding.inputLayoutPeso.error = "Obrigatório"
                camposValidos = false
            }
            if (binding.editTextAltura.text.isNullOrBlank()) {
                binding.inputLayoutAltura.error = "Obrigatório"
                camposValidos = false
            }
            if (binding.radioGroupCastrado.checkedRadioButtonId == -1) {
                Toast.makeText(context, "Informe se o pet é castrado", Toast.LENGTH_SHORT).show()
                camposValidos = false
            }
        }

        return camposValidos
    }

    private fun mostrarDatePicker() {

        val constraintsBuilder = CalendarConstraints.Builder()
        val hojeEmUtc = MaterialDatePicker.todayInUtcMilliseconds()
        val validator = DateValidatorPointBackward.now()

        constraintsBuilder.setEnd(hojeEmUtc)
        constraintsBuilder.setValidator(validator)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data de nascimento")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { dataTimestamp ->
            val data = Date(dataTimestamp)
            binding.editTextDataNascimento.setText(formatadorDeData.format(data))
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}