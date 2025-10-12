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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentAnimalCadastroBinding
import br.com.caiorodri.agenpet.model.animal.Animal
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AnimalCadastroFragment : Fragment() {

    private var _binding: FragmentAnimalCadastroBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnimalCadastroViewModel by viewModels();
    private val args: AnimalCadastroFragmentArgs by navArgs();

    private var animalParaEdicao: Animal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimalCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animalParaEdicao = args.animal;
        setupObservers();
        setupListeners();
        setupUI();

    }

    private fun setupUI() {
        val animal = animalParaEdicao;

        if (animal != null) {

            (activity as? AppCompatActivity)?.supportActionBar?.title = "Editar Pet"
            binding.textViewIdAnimal.text = "ID do Pet: ${animal.id}"
            binding.textViewIdAnimal.isVisible = true

            binding.editTextNome.setText(animal.nome)
            binding.editTextDataNascimento.setText(animal.dataNascimento)
            binding.editTextDescricao.setText(animal.descricao)

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

            (activity as? AppCompatActivity)?.supportActionBar?.title = "Novo Pet"
            binding.textViewIdAnimal.isVisible = false
            binding.labelInfoAdicional.isVisible = false
            binding.buttonSalvar.text = "Salvar"

        }
    }

    private fun setupListeners() {

        binding.inputLayoutDataNascimento.setEndIconOnClickListener {
            mostrarDatePicker()
        }
        binding.editTextDataNascimento.setOnClickListener {
            mostrarDatePicker()
        }

        binding.autoCompleteEspecie.setOnItemClickListener { parent, view, position, id ->
            val nomeEspecie = parent.getItemAtPosition(position) as String
            val especieSelecionada = viewModel.especies.value?.find { it.nome == nomeEspecie }

            binding.autoCompleteRaca.text = null
            binding.menuRaca.isEnabled = (especieSelecionada != null)
            viewModel.filtrarRacasPorEspecie(especieSelecionada)
        }

        binding.buttonSalvar.setOnClickListener {

            Toast.makeText(context, "Salvando dados...", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()

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

    private fun mostrarDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data de nascimento")
            .build()
        datePicker.addOnPositiveButtonClickListener { dataTimestamp ->
            val data = Date(dataTimestamp)
            val formatador = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.editTextDataNascimento.setText(formatador.format(data))
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}