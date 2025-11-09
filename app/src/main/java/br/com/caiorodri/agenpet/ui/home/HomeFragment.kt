package br.com.caiorodri.agenpet.ui.home;

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.viewpager2.widget.ViewPager2
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentHomeBinding
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.ui.adapter.AgendamentoAdapter
import br.com.caiorodri.agenpet.ui.adapter.PropagandaAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null;
    private val binding get() = _binding!!;
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();
    private val viewModel: HomeViewModel by viewModels();
    private lateinit var agendamentoAdapter: AgendamentoAdapter;
    private lateinit var propagandaAdapter: PropagandaAdapter;
    private val listaPropagandas = listOf(
        R.drawable.pg_cobasi,
        R.drawable.pg_petz,
        R.drawable.pg_petlove
    );
    private val autoScrollHandler = Handler(Looper.getMainLooper());
    private var autoScrollRunnable: Runnable? = null;

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
        setupViewPagerPropaganda();

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

    private fun setupViewPagerPropaganda() {

        propagandaAdapter = PropagandaAdapter(listaPropagandas);
        binding.viewPagerPropaganda.adapter = propagandaAdapter;

        val tabLayout = binding.tabLayoutIndicador;
        val viewPager = binding.viewPagerPropaganda;

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
        }.attach();


        tabLayout.post {

            val tabsContainer = tabLayout.getChildAt(0) as ViewGroup;

            val dotSizeSelected = (10 * resources.displayMetrics.density).toInt();
            val margin = (6 * resources.displayMetrics.density).toInt();

            for (i in 0 until tabsContainer.childCount) {

                val tabView = tabsContainer.getChildAt(i);

                val params = tabView.layoutParams as ViewGroup.MarginLayoutParams;

                params.width = dotSizeSelected
                params.height = dotSizeSelected

                params.setMargins(margin, 0, margin, 0);

                tabView.layoutParams = params;
                tabView.requestLayout();

            }
        }

        binding.viewPagerPropaganda.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    stopAutoScroll();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    startAutoScroll();
                }
            }
        })
    }

    private fun startAutoScroll() {

        stopAutoScroll();
        autoScrollRunnable = Runnable {
            var currentItem = binding.viewPagerPropaganda.currentItem;
            currentItem++;

            if (currentItem >= propagandaAdapter.itemCount) {
                currentItem = 0;
            }
            binding.viewPagerPropaganda.setCurrentItem(currentItem, true);

            autoScrollHandler.postDelayed(autoScrollRunnable!!, 5000);
        }

        autoScrollHandler.postDelayed(autoScrollRunnable!!, 5000);
    }

    private fun stopAutoScroll() {
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume();
        startAutoScroll();
    }

    override fun onPause() {
        super.onPause();
        stopAutoScroll();
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}