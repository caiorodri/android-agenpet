package br.com.caiorodri.agenpet.ui.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController;
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.databinding.FragmentFuncionarioBinding;
import br.com.caiorodri.agenpet.ui.adapter.FuncionarioAdapter;

class FuncionarioFragment : Fragment() {

    private var _binding: FragmentFuncionarioBinding? = null;
    private val binding get() = _binding!!;

    private val viewModel: FuncionarioViewModel by viewModels();
    private lateinit var adapter: FuncionarioAdapter;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFuncionarioBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupListeners();
        setupObservers();

        viewModel.listarFuncionarios();
    }

    private fun setupRecyclerView() {
        adapter = FuncionarioAdapter { funcionario ->
            val action = FuncionarioFragmentDirections.actionFuncionarioFragmentToFuncionarioCadastroFragment(funcionario);
            findNavController().navigate(action);

        }

        binding.recyclerViewFuncionarios.layoutManager = LinearLayoutManager(context);
        binding.recyclerViewFuncionarios.adapter = adapter;
    }

    private fun setupListeners() {
        binding.fabAddFuncionario.setOnClickListener {
            findNavController().navigate(R.id.action_funcionarioFragment_to_funcionarioCadastroFragment);
        };

        binding.swipeRefreshFuncionarios.setOnRefreshListener {
            viewModel.listarFuncionarios();
        };
    }

    private fun setupObservers() {
        viewModel.funcionarios.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista);
            binding.textInfoVazio.isVisible = lista.isEmpty();
        };

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefreshFuncionarios.isRefreshing = loading;

            if (loading && adapter.itemCount == 0) {
                binding.progressBarCentro.isVisible = true;
            } else {
                binding.progressBarCentro.isVisible = false;
            }
        };

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro != null) {
                Toast.makeText(context, erro, Toast.LENGTH_SHORT).show();
            }
        };
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}