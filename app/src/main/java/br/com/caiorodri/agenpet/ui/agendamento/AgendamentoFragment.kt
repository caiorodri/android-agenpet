package br.com.caiorodri.agenpet.ui.agendamento;

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.caiorodri.agenpet.databinding.FragmentAgendamentoBinding
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.ui.adapter.AgendamentoCompletoAdapter
import br.com.caiorodri.agenpet.ui.home.ClienteHomeActivity
import br.com.caiorodri.agenpet.ui.home.ClienteHomeSharedViewModel
import br.com.caiorodri.agenpet.ui.home.HomeActivity
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel

class AgendamentoFragment : Fragment() {

    private var _binding: FragmentAgendamentoBinding? = null;
    private val binding get() = _binding!!;
    private val viewModel: AgendamentoViewModel by viewModels();
    private lateinit var agendamentoAdapter: AgendamentoCompletoAdapter;
    private lateinit var usuarioLogadoLiveData: LiveData<Usuario>;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendamentoBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        val activity = requireActivity();

        if(activity is ClienteHomeActivity){

            val sharedViewModel = ViewModelProvider(activity)[ClienteHomeSharedViewModel::class.java];
            usuarioLogadoLiveData = sharedViewModel.usuarioLogado;

        } else if (activity is HomeActivity){

            val sharedViewModel = ViewModelProvider(activity)[HomeSharedViewModel::class.java];
            usuarioLogadoLiveData = sharedViewModel.usuarioLogado;

        } else {

            throw IllegalStateException("Activity não suportada");

        }

        setupRecyclerView();
        setupListeners();
        setupObservers();
    }

    private fun setupRecyclerView() {
        agendamentoAdapter = AgendamentoCompletoAdapter { agendamentoClicado ->
            val action = AgendamentoFragmentDirections.actionAgendamentoFragmentToAgendamentoCadastroFragment(agendamentoClicado);
            findNavController().navigate(action);
        };

        binding.recyclerViewAgendamentos.adapter = agendamentoAdapter;
    }

    private fun setupListeners() {
        binding.searchViewAgendamentos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false;

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filtrarAgendamentos(newText);
                return true;
            }
        });

        binding.fabAddAgendamento.setOnClickListener {
            val action = AgendamentoFragmentDirections.actionAgendamentoFragmentToAgendamentoCadastroFragment(null);
            findNavController().navigate(action);
        };

        binding.swipeRefreshLayoutAgendamentos.setOnRefreshListener {
            usuarioLogadoLiveData.value?.let { usuario ->
                viewModel.carregarAgendamentos(usuario.id!!);
            };
        };
    }

    private fun setupObservers() {

        usuarioLogadoLiveData.observe(viewLifecycleOwner) { usuario ->

            viewModel.setAgendamentosIniciais(usuario.agendamentos ?: emptyList());
            
            if (usuario?.id != null) {
                viewModel.carregarAgendamentos(usuario.id);
            }

        }

        viewModel.agendamentos.observe(viewLifecycleOwner) { lista ->
            if (lista.isEmpty()) {
                binding.recyclerViewAgendamentos.isVisible = false;
                binding.layoutInfoSemAgendamentos.isVisible = true;
            } else {
                binding.recyclerViewAgendamentos.isVisible = true;
                binding.layoutInfoSemAgendamentos.isVisible = false;
            }
            agendamentoAdapter.submitList(lista);
        };

        viewModel.isLoading.observe(viewLifecycleOwner) { estaCarregando ->
            binding.progressBarAgendamentos.isVisible = estaCarregando && agendamentoAdapter.itemCount > 0;

            if (!estaCarregando) {
                binding.swipeRefreshLayoutAgendamentos.isRefreshing = false;
            }
        };

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            erro?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show();
            }
        };
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}