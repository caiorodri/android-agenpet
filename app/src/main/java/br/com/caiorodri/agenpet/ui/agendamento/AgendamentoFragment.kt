package br.com.caiorodri.agenpet.ui.agendamento

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
import br.com.caiorodri.agenpet.databinding.FragmentAgendamentoBinding
import br.com.caiorodri.agenpet.ui.adapter.AgendamentoCompletoAdapter
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel

class AgendamentoFragment : Fragment() {

    private var _binding: FragmentAgendamentoBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: HomeSharedViewModel by activityViewModels()
    private val viewModel: AgendamentoViewModel by viewModels()
    private lateinit var agendamentoAdapter: AgendamentoCompletoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        agendamentoAdapter = AgendamentoCompletoAdapter()
        binding.recyclerViewAgendamentos.adapter = agendamentoAdapter
    }

    private fun setupListeners() {
        binding.searchViewAgendamentos.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filtrarAgendamentos(newText)
                return true
            }
        })

        binding.fabAddAgendamento.setOnClickListener {
            Toast.makeText(context, "Abrir tela de novo agendamento", Toast.LENGTH_SHORT).show()
        }

        binding.swipeRefreshLayoutAgendamentos.setOnRefreshListener {
            binding.swipeRefreshLayoutAgendamentos.isRefreshing = false
            sharedViewModel.usuarioLogado.value?.let { usuario ->
                viewModel.carregarAgendamentos(usuario.id!!)
            }
        }
    }

    private fun setupObservers() {
        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->
            viewModel.setAgendamentosIniciais(usuario.agendamentos ?: emptyList())
            viewModel.carregarAgendamentos(usuario.id!!)
        }

        viewModel.agendamentos.observe(viewLifecycleOwner) { lista ->
            agendamentoAdapter.submitList(lista)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { estaCarregando ->
            binding.progressBarAgendamentos.isVisible = estaCarregando
        }

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            erro?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}