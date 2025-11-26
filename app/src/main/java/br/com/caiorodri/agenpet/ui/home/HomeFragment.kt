package br.com.caiorodri.agenpet.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.activityViewModels;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import br.com.caiorodri.agenpet.databinding.FragmentHomeBinding;
import br.com.caiorodri.agenpet.ui.adapter.AgendamentoAdapter;
import br.com.caiorodri.agenpet.R;

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
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupListeners();
        setupObservers();
    }

    private fun setupRecyclerView() {

        agendamentoAdapter = AgendamentoAdapter { agendamento ->

            val action = HomeFragmentDirections.actionHomeProfissionalFragmentToAgendamentoCadastroFragment(agendamento);
            findNavController().navigate(action);


        };

        binding.recyclerViewAgenda.layoutManager = LinearLayoutManager(context);
        binding.recyclerViewAgenda.adapter = agendamentoAdapter;
    }

    private fun setupListeners() {

        binding.cardCadastroFuncionarios.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeProfissionalFragmentToFuncionarioCadastroFragment();

            findNavController().navigate(action);

        }
    }

    private fun setupObservers() {

        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->

            val partesDoNome = usuario.nome.split(" ");
            val primeiroNome = partesDoNome.firstOrNull() ?: "";

            var saudacaoFinal = getString(R.string.saudacao_simples, primeiroNome);

            val perfil = usuario.perfil?.nome?.uppercase();

            if (perfil == "ADMINISTRADOR") {

                binding.grupoAdmin.isVisible = true;
                binding.grupoAgenda.isVisible = false;

            } else {

                if(perfil == "VETERINARIO"){

                    val segundoNome = partesDoNome[1];
                    saudacaoFinal = getString(R.string.saudacao_composta, primeiroNome, segundoNome);

                }

                binding.grupoAdmin.isVisible = false;
                binding.grupoAgenda.isVisible = true;

                viewModel.carregarDadosHome(usuario);
            }

            binding.textBoasVindas.text = saudacaoFinal;

        }

        viewModel.agendamentosDia.observe(viewLifecycleOwner) { lista ->

            agendamentoAdapter.submitList(lista);

            binding.textSemAgendamentos.isVisible = lista.isEmpty();
            binding.recyclerViewAgenda.isVisible = lista.isNotEmpty();

        }

        viewModel.erro.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}