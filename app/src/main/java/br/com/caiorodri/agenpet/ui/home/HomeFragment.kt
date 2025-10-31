package br.com.caiorodri.agenpet.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.caiorodri.agenpet.databinding.FragmentHomeBinding
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.ui.adapter.AgendamentoAdapter
import androidx.navigation.fragment.findNavController
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null;
    private val binding get() = _binding!!;
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();
    private val viewModel: HomeViewModel by viewModels();
    private lateinit var agendamentoAdapter: AgendamentoAdapter;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView();
        setupObservers();

        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d("HomeFragment", "Iniciando atualização (pull-to-refresh)...")

            (activity as? HomeActivity)?.carregarDadosDoUsuario();
        }

    }

    private fun setupRecyclerView() {
        agendamentoAdapter = AgendamentoAdapter{ agendamentoClicado ->
            val action = HomeFragmentDirections.actionHomeFragmentToAgendamentoCadastroFragment(agendamentoClicado)
            findNavController().navigate(action)
        }

        binding.recyclerViewRecentes.apply {
            layoutManager = LinearLayoutManager(context);
            adapter = agendamentoAdapter;
        }
    }

    private fun setupObservers() {
        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->
            viewModel.setAgendamentosIniciais(usuario.agendamentos ?: emptyList());
        }

        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading;
        }

        viewModel.agendamentos.observe(viewLifecycleOwner) { listaDeAgendamentos ->
            Log.i("HomeFragment", "Agendamentos atualizados: ${listaDeAgendamentos.size}");

            agendamentoAdapter.submitList(listaDeAgendamentos.take(3));

            val ultimoAgendamento = listaDeAgendamentos.firstOrNull();
            updateUltimoAgendamentoCard(ultimoAgendamento);

        }

        viewModel.erro.observe(viewLifecycleOwner) { mensagemDeErro ->
            if (mensagemDeErro != null) {
                Toast.makeText(context, mensagemDeErro, Toast.LENGTH_LONG).show();
            }

        }
    }

    private fun setupInitialUI(usuario: Usuario) {
        val ultimoAgendamento = usuario.agendamentos?.firstOrNull()
        updateUltimoAgendamentoCard(ultimoAgendamento)
    }

    private fun updateUltimoAgendamentoCard(agendamento: Agendamento?) {
        val cardRoot = binding.includeItemUltimoAgendamento.root
        cardRoot.isVisible = (agendamento != null)

        if (agendamento != null) {
            with(binding.includeItemUltimoAgendamento) {
                textViewUltimoVetNome.text = agendamento.veterinario.nome
                textViewUltimoAnimalNome.text = agendamento.animal.nome

                val data = Date(agendamento.dataAgendamentoInicio)
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())

                textViewData.text = outputFormat.format(data)
                textViewHorario.text = formatoHora.format(data)
            }

            cardRoot.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAgendamentoCadastroFragment(agendamento)
                findNavController().navigate(action)
            }

        } else {
            cardRoot.setOnClickListener(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}