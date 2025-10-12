package br.com.caiorodri.agenpet.ui.animal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.com.caiorodri.agenpet.databinding.FragmentAnimalBinding
import br.com.caiorodri.agenpet.ui.adapter.AnimalAdapter
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel

class AnimalFragment : Fragment() {

    private var _binding: FragmentAnimalBinding? = null;
    private val binding get() = _binding!!;
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();
    private val viewModel: AnimalViewModel by viewModels();
    private lateinit var animalAdapter: AnimalAdapter;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupListeners();
        setupObservers();

    }

    private fun setupRecyclerView() {
        animalAdapter = AnimalAdapter { animalClicado ->
            val action = AnimalFragmentDirections.actionAnimalFragmentToCadastroAnimalFragment(animalClicado);
            findNavController().navigate(action);
        }
        binding.recyclerViewAnimais.adapter = animalAdapter;
    }

    private fun setupListeners() {
        binding.searchViewAnimais.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filtrarAnimais(newText);
                return true;
            }
        })

        binding.fabAddAnimal.setOnClickListener {
            val action = AnimalFragmentDirections.actionAnimalFragmentToCadastroAnimalFragment(null);
            findNavController().navigate(action);
        }

        binding.swipeRefreshLayoutAnimais.setOnRefreshListener {

            binding.swipeRefreshLayoutAnimais.isRefreshing = false;
            sharedViewModel.usuarioLogado.value?.let { usuario ->
                viewModel.carregarAnimais(usuario.id!!);
            }

        }
    }

    private fun setupObservers() {

        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->
            viewModel.carregarAnimais(usuario.id!!);
        }

        viewModel.animais.observe(viewLifecycleOwner) { lista ->
            animalAdapter.submitList(lista);
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { estaCarregando ->
            binding.progressBarAnimais.isVisible = estaCarregando;
        }

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            erro?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}