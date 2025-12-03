package br.com.caiorodri.agenpet.ui.sobre

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentDesenvolvedorBinding
import br.com.caiorodri.agenpet.model.sobre.Desenvolvedor
import br.com.caiorodri.agenpet.ui.adapter.DesenvolvedorAdapter
import androidx.core.net.toUri

class DesenvolvedorFragment : Fragment() {

    private var _binding: FragmentDesenvolvedorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDesenvolvedorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_desenvolvedores)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val listaDevs = listOf(
            Desenvolvedor(
                "Caio Rodrigues",
                "Full-Stack + Android Developer",
                R.drawable.dev_caio,
                "https://www.linkedin.com/in/caio-rodri/"
            ),
            Desenvolvedor(
                "Caio Teles",
                "Data Analyst",
                R.drawable.dev_caio_teles,
                "https://www.linkedin.com/in/telescaio"
            ),
            Desenvolvedor(
                "Felipe Geremias",
                "Full-Stack Developer",
                R.drawable.dev_felipe,
                "https://www.linkedin.com/in/felipe-geremias-s"
            ),
            Desenvolvedor(
                "Guilherme Correa",
                "Data Science",
                R.drawable.dev_guilherme,
                "https://www.linkedin.com/in/guilherme-correa-971667223/"
            )
        )

        val adapter = DesenvolvedorAdapter(listaDevs) { url ->
            abrirLinkedin(url);
        }

        binding.recyclerDesenvolvedores.adapter = adapter;
    }

    private fun abrirLinkedin(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri());
            startActivity(intent);
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.erro_abrir_link), Toast.LENGTH_SHORT).show();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}