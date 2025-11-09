package br.com.caiorodri.agenpet.ui.home;

import android.graphics.drawable.Drawable
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentHomeBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.ui.adapter.AgendamentoAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        setupObservers();

        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.d("HomeFragment", "Iniciando atualização...");
            (activity as? HomeActivity)?.carregarDadosDoUsuario();
        }

    }

    private fun setupRecyclerView() {
        agendamentoAdapter = AgendamentoAdapter{ agendamentoClicado ->
            val action = HomeFragmentDirections.actionHomeFragmentToAgendamentoCadastroFragment(agendamentoClicado);
            findNavController().navigate(action);
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

        viewModel.agendamentosRecentes.observe(viewLifecycleOwner) { listaRecentes ->
            Log.i("HomeFragment", "Agendamentos recentes atualizados: ${listaRecentes.size}");

            if (listaRecentes.isEmpty()) {
                binding.recyclerViewRecentes.isVisible = false;
                binding.textViewInfoRecentes.isVisible = true;
            } else {
                binding.recyclerViewRecentes.isVisible = true;
                binding.textViewInfoRecentes.isVisible = false;
            }

            agendamentoAdapter.submitList(listaRecentes);
        }

        viewModel.proximoAgendamento.observe(viewLifecycleOwner) { proximo ->
            updateProximoAgendamentoCard(proximo);
        }

        viewModel.erro.observe(viewLifecycleOwner) { mensagemDeErro ->
            if (mensagemDeErro != null) {
                Toast.makeText(context, mensagemDeErro, Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun updateProximoAgendamentoCard(agendamento: Agendamento?) {
        val cardRoot = binding.includeItemUltimoAgendamento.root;

        cardRoot.isVisible = (agendamento != null);
        binding.textViewInfoProximo.isVisible = (agendamento == null);

        binding.tituloUltimoAgendamento.text = getString(R.string.titulo_proximo_agendamento);

        if (agendamento != null) {

            with(binding.includeItemUltimoAgendamento) {
                textViewUltimoVetNome.text = agendamento.veterinario.nome;
                textViewUltimoAnimalNome.text = agendamento.animal.nome;

                val data = Date(agendamento.dataAgendamentoInicio);
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault());

                textViewData.text = outputFormat.format(data);
                textViewHorario.text = formatoHora.format(data);

                progressBarFotoUltimo.visibility = View.VISIBLE;

                Glide.with(this@HomeFragment)
                    .load(agendamento.animal.urlImagem)
                    .placeholder(R.drawable.ic_pet)
                    .error(R.drawable.ic_pet)
                    .circleCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            progressBarFotoUltimo.visibility = View.GONE;
                            return false;
                        }
                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: com.bumptech.glide.load.DataSource, isFirstResource: Boolean): Boolean {
                            progressBarFotoUltimo.visibility = View.GONE;
                            return false;
                        }
                    })
                    .into(imageViewPetFoto);

            }

            cardRoot.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAgendamentoCadastroFragment(agendamento);
                findNavController().navigate(action);
            }

        } else {
            cardRoot.setOnClickListener(null);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}