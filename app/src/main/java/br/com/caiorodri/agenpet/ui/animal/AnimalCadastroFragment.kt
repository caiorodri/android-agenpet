package br.com.caiorodri.agenpet.ui.animal

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import br.com.caiorodri.agenpet.model.animal.AnimalResponse
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.model.animal.Raca
import br.com.caiorodri.agenpet.model.animal.Sexo
import br.com.caiorodri.agenpet.ui.home.HomeActivity
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class AnimalCadastroFragment : Fragment() {

    private var _binding: FragmentAnimalCadastroBinding? = null;
    private val binding get() = _binding!!;
    private val viewModel: AnimalCadastroViewModel by viewModels();
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();
    private val args: AnimalCadastroFragmentArgs by navArgs();
    private var animalParaEdicao: Animal? = null;
    private val formatadorDeData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };

    private lateinit var especieAdapter: ArrayAdapter<String>;
    private lateinit var racaAdapter: ArrayAdapter<String>;

    private val uiHandler = Handler(Looper.getMainLooper());

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimalCadastroBinding.inflate(inflater, container, false);

        especieAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>());
        racaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>());
        binding.autoCompleteEspecie.setAdapter(especieAdapter);
        binding.autoCompleteRaca.setAdapter(racaAdapter);

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        animalParaEdicao = args.animal;

        setupUIBase();
        setupListeners();
        setupObservers();
    }

    private fun setupUIBase() {
        val animal = animalParaEdicao;
        if (animal != null) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_editar_animal);
            binding.textViewIdAnimal.text = getString(R.string.placeholder_id_animal, animal.id ?: 0);
            binding.textViewIdAnimal.isVisible = true;
            binding.buttonRemover.isVisible = true;

            binding.editTextNome.setText(animal.nome);
            binding.editTextDescricao.setText(animal.descricao);

            animal.dataNascimento?.let { timestamp ->
                binding.editTextDataNascimento.setText(formatadorDeData.format(Date(timestamp)));
            };

            if (animal.sexo?.nome == getString(R.string.option_macho)) {
                binding.toggleButtonGroupSexo.check(R.id.button_macho);
            } else if (animal.sexo?.nome == getString(R.string.option_femea)){
                binding.toggleButtonGroupSexo.check(R.id.button_femea);
            }

            binding.editTextPeso.setText(animal.peso?.toString());
            binding.editTextAltura.setText(animal.altura?.toString());
            if (animal.castrado == true) {
                binding.radioCastradoSim.isChecked = true;
            } else {
                binding.radioCastradoNao.isChecked = true;
            }

            binding.inputLayoutPeso.isEnabled = false;
            binding.inputLayoutAltura.isEnabled = false;
            binding.radioGroupCastrado.children.forEach { it.isEnabled = false };
            binding.labelInfoAdicional.isVisible = true;

            binding.buttonSalvar.text = getString(R.string.button_atualizar);
        } else {
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.label_cadastro_pet);
            binding.textViewIdAnimal.isVisible = false;
            binding.buttonRemover.isVisible = false;
            binding.labelInfoAdicional.isVisible = true;
            binding.buttonSalvar.text = getString(R.string.button_salvar);
            binding.inputLayoutPeso.isEnabled = true;
            binding.inputLayoutAltura.isEnabled = true;
            binding.radioGroupCastrado.children.forEach { it.isEnabled = true };
            binding.menuRaca.isEnabled = false;
        }
    }

    private fun setupListeners() {
        binding.inputLayoutDataNascimento.setEndIconOnClickListener { mostrarDatePicker(); };
        binding.editTextDataNascimento.setOnClickListener { mostrarDatePicker(); };

        binding.autoCompleteEspecie.setOnItemClickListener { parent, _, position, _ ->
            val nomeEspecie = parent.getItemAtPosition(position) as String;
            val especieSelecionada = viewModel.especies.value?.find { it.nome == nomeEspecie };

            binding.autoCompleteRaca.setText(null, false);
            viewModel.filtrarRacasPorEspecie(especieSelecionada);
        };

        binding.buttonSalvar.setOnClickListener {
            salvarDadosDoAnimal();
        };

        binding.buttonRemover.setOnClickListener {
            mostrarDialogoDelecao()
        }
    }

    private fun setupObservers() {
        viewModel.especies.observe(viewLifecycleOwner) { especies ->
            val nomesDasEspecies = especies.map { it.nome };
            uiHandler.post {
                especieAdapter.clear();
                especieAdapter.addAll(nomesDasEspecies);
                especieAdapter.notifyDataSetChanged();

                if (animalParaEdicao != null) {
                    val especieAtualNome = animalParaEdicao?.raca?.especie?.nome;
                    if (especieAtualNome != null && nomesDasEspecies.contains(especieAtualNome)) {
                        binding.autoCompleteEspecie.setText(especieAtualNome, false);
                        val especieInicial = especies.find { it.nome == especieAtualNome };
                        viewModel.filtrarRacasPorEspecie(especieInicial);
                    }
                }
            };
        };

        viewModel.racasFiltradas.observe(viewLifecycleOwner) { racas ->
            val nomesDasRacas = racas.map { it.nome };
            uiHandler.post {
                racaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesDasRacas);
                binding.autoCompleteRaca.setAdapter(racaAdapter);
                binding.menuRaca.isEnabled = racas.isNotEmpty();

                if (animalParaEdicao != null) {
                    val racaAtualNome = animalParaEdicao?.raca?.nome;
                    if (racaAtualNome != null && nomesDasRacas.contains(racaAtualNome)) {
                        binding.autoCompleteRaca.setText(racaAtualNome, false);
                    } else {
                        binding.autoCompleteRaca.setText(null, false);
                    }
                } else {
                    binding.autoCompleteRaca.setText(null, false);
                }
            };
        };

        viewModel.isLoadingDadosIniciais.observe(viewLifecycleOwner) { isLoading ->
            uiHandler.post {
                binding.progressBarRacas?.isVisible = isLoading;
                binding.menuEspecie.isEnabled = !isLoading;
                if(isLoading) {
                    binding.menuRaca.isEnabled = false;
                }
            };
        };

        viewModel.erroDadosIniciais.observe(viewLifecycleOwner) { erro ->
            erro?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show(); };
        };

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.savingOverlay.isVisible = isLoading
            binding.buttonSalvar.isEnabled = !isLoading
            binding.buttonRemover.isEnabled = !isLoading
        }

        viewModel.actionError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                viewModel.resetActionError()
            }
        }

        viewModel.animalSalvoComSucesso.observe(viewLifecycleOwner) { animalResponseSalvo ->
            animalResponseSalvo ?: return@observe

            Toast.makeText(context, getString(R.string.toast_animal_salvo_sucesso), Toast.LENGTH_SHORT).show()

            val animalConvertido = Animal(animalResponseSalvo)
            val animalFinal = animalConvertido.copy(
                agendamentos = animalParaEdicao?.agendamentos
            )

            sharedViewModel.atualizarAnimalLocalmente(animalFinal)
            (activity as? HomeActivity)?.carregarDadosDoUsuario()

            viewModel.resetAnimalSalvo()
            findNavController().popBackStack()
        }

        viewModel.animalRemovidoComSucesso.observe(viewLifecycleOwner) { removido ->
            removido ?: return@observe

            Toast.makeText(context, getString(R.string.toast_animal_removido_sucesso), Toast.LENGTH_SHORT).show()

            animalParaEdicao?.id?.let {
                sharedViewModel.removerAnimalLocalmente(it)
            }

            (activity as? HomeActivity)?.carregarDadosDoUsuario()

            viewModel.resetAnimalRemovido()
            findNavController().popBackStack()
        }
    }

    private fun mostrarDialogoDelecao() {
        val animalId = animalParaEdicao?.id
        val animalNome = animalParaEdicao?.nome
        if (animalId == null || animalNome == null) return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_remover_animal))
            .setMessage(getString(R.string.dialog_message_remover_animal, animalNome))
            .setNegativeButton(getString(R.string.dialog_button_cancelar)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_button_remover)) { dialog, _ ->
                viewModel.removerAnimal(animalId)
                dialog.dismiss()
            }
            .show()
    }

    private fun salvarDadosDoAnimal() {

        if (!validarCampos()) {
            Toast.makeText(context, getString(R.string.toast_preencher_campos_obrigatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        val dono = sharedViewModel.usuarioLogado.value;

        if (dono == null) {
            Toast.makeText(context, getString(R.string.toast_erro_usuario_nao_encontrado), Toast.LENGTH_LONG).show();
            return;
        }

        val dataNascimentoString = binding.editTextDataNascimento.text.toString();
        val dataNascimentoTimestamp = if (dataNascimentoString.isNotBlank()) {
            try {
                formatadorDeData.parse(dataNascimentoString)?.time;
            } catch (e: Exception) {
                null;
            }
        } else {
            null;
        };

        val nome = binding.editTextNome.text.toString().trim();
        val descricao = binding.editTextDescricao.text.toString().trim();

        val nomeRacaSelecionada = binding.autoCompleteRaca.text.toString();
        val raca = viewModel.listaCompletaRacas.find { it.nome == nomeRacaSelecionada };

        val nomeSexo = if (binding.toggleButtonGroupSexo.checkedButtonId == R.id.button_macho) getString(R.string.option_macho) else getString(R.string.option_femea);
        val sexo = viewModel.sexos.value?.find { it.nome == nomeSexo };

        val peso = if (animalParaEdicao != null) animalParaEdicao?.peso else binding.editTextPeso.text.toString().toDoubleOrNull();
        val altura = if (animalParaEdicao != null) animalParaEdicao?.altura else binding.editTextAltura.text.toString().toDoubleOrNull();
        val castrado = if (animalParaEdicao != null) animalParaEdicao?.castrado else binding.radioGroupCastrado.checkedRadioButtonId == R.id.radio_castrado_sim;

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
        );

        viewModel.salvarAnimal(animal);
    }

    private fun validarCampos(): Boolean {
        var camposValidos = true;

        binding.inputLayoutNome.error = null;
        binding.menuEspecie.error = null;
        binding.menuRaca.error = null;
        binding.inputLayoutPeso.error = null;
        binding.inputLayoutAltura.error = null;

        if (binding.editTextNome.text.isNullOrBlank()) {
            binding.inputLayoutNome.error = getString(R.string.erro_nome_obrigatorio);
            camposValidos = false;
        }

        if (binding.autoCompleteEspecie.text.isNullOrBlank()) {
            binding.menuEspecie.error = getString(R.string.erro_especie_obrigatoria);
            camposValidos = false;
        }

        if (binding.menuRaca.isEnabled && binding.autoCompleteRaca.text.isNullOrBlank()) {
            binding.menuRaca.error = getString(R.string.erro_raca_obrigatoria);
            camposValidos = false;
        }

        if (binding.toggleButtonGroupSexo.checkedButtonId == View.NO_ID) {
            Toast.makeText(context, getString(R.string.toast_selecionar_sexo), Toast.LENGTH_SHORT).show();
            camposValidos = false;
        }

        if (animalParaEdicao == null) {
            if (binding.editTextPeso.text.isNullOrBlank()) {
                binding.inputLayoutPeso.error = getString(R.string.erro_obrigatorio);
                camposValidos = false;
            }
            if (binding.editTextAltura.text.isNullOrBlank()) {
                binding.inputLayoutAltura.error = getString(R.string.erro_obrigatorio);
                camposValidos = false;
            }
            if (binding.radioGroupCastrado.checkedRadioButtonId == -1) {
                Toast.makeText(context, getString(R.string.toast_informar_castrado), Toast.LENGTH_SHORT).show();
                camposValidos = false;
            }
        }

        return camposValidos;
    }

    private fun mostrarDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder();
        val hojeEmUtc = MaterialDatePicker.todayInUtcMilliseconds();
        val validator = DateValidatorPointBackward.now();

        constraintsBuilder.setEnd(hojeEmUtc);
        constraintsBuilder.setValidator(validator);

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.titulo_datepicker_data_nascimento))
            .setCalendarConstraints(constraintsBuilder.build())
            .build();

        datePicker.addOnPositiveButtonClickListener { dataTimestamp ->
            val data = Date(dataTimestamp);
            binding.editTextDataNascimento.setText(formatadorDeData.format(data));
        };

        datePicker.show(childFragmentManager, "DATE_PICKER");
    }


    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
        uiHandler.removeCallbacksAndMessages(null);
    }
}