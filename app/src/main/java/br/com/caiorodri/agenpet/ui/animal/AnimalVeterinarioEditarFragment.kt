package br.com.caiorodri.agenpet.ui.animal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController;
import androidx.navigation.fragment.navArgs;
import br.com.caiorodri.agenpet.databinding.FragmentAnimalVeterinarioEditarBinding
import br.com.caiorodri.agenpet.model.animal.Animal;
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import br.com.caiorodri.agenpet.R;

class AnimalVeterinarioEditarFragment : Fragment() {

    private var _binding: FragmentAnimalVeterinarioEditarBinding? = null;
    private val binding get() = _binding!!;

    private val viewModel: AnimalVeterinarioEditarViewModel by viewModels();
    private val args: AnimalVeterinarioEditarFragmentArgs by navArgs();
    private var animalAtual: Animal? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalVeterinarioEditarBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
        setupObservers();

        popularCamposIniciais(args.animal.nome);

        val idAnimal = args.animal.id;

        viewModel.recuperarAnimal(idAnimal!!);

    }

    private fun popularCamposIniciais(nome: String){

        binding.textNomeAnimal.text = nome;

    }

    private fun popularCampos(animal: Animal) {

        animalAtual = animal;

        binding.textNomeAnimal.text = animal.nome;

        if(animal.raca?.getNomeTraduzido(requireContext()) == R.string.raca_desconhecido.toString()){

            binding.textRacaAnimal.text = animal.raca?.getNomeTraduzido(requireContext());

        } else {

            binding.textRacaAnimal.text = "${animal.raca?.especie?.getNomeTraduzido(requireContext())} - ${animal.raca?.getNomeTraduzido(requireContext())}";

        }

        if (animal.peso != null) {
            binding.editTextPeso.setText(animal.peso.toString());
        }
        if (animal.altura != null) {
            binding.editTextAltura.setText(animal.altura.toString());
        }

        binding.switchCastrado.isChecked = animal.castrado!!;
    }

    private fun setupListeners() {
        binding.buttonSalvarDadosClinicos.setOnClickListener {
            salvarDados();
        }
    }

    private fun setupObservers() {

        viewModel.animal.observe(viewLifecycleOwner) { animalRecuperado ->

            if (animalRecuperado != null) {

                popularCampos(animalRecuperado);

            } else {

                Toast.makeText(context, getString(R.string.erro_animal_nao_encontrado), Toast.LENGTH_SHORT).show();

            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->

            binding.loadingOverlay.isVisible = loading;
            binding.buttonSalvarDadosClinicos.isEnabled = !loading;

        }

        viewModel.sucessoAtualizacao.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                Toast.makeText(context, getString(R.string.sucesso_dados_clinicos_atualizados), Toast.LENGTH_SHORT).show();
                viewModel.resetSucesso();
                findNavController().popBackStack();
            }
        };

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro != null) {
                Toast.makeText(context, erro, Toast.LENGTH_LONG).show();
            }
        };
    }

    private fun salvarDados() {

        if (animalAtual == null) return;

        val pesoStr = binding.editTextPeso.text.toString();
        val alturaStr = binding.editTextAltura.text.toString();

        val novoPeso = if (pesoStr.isNotBlank()) pesoStr.toDoubleOrNull() else null;
        val novaAltura = if (alturaStr.isNotBlank()) alturaStr.toDoubleOrNull() else null;
        val isCastrado = binding.switchCastrado.isChecked;

        val animalAtualizado = animalAtual!!.copy(
            peso = novoPeso,
            altura = novaAltura,
            castrado = isCastrado
        );

        viewModel.atualizarDadosClinicos(animalAtualizado);
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}