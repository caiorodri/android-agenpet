package br.com.caiorodri.agenpet.ui.sobre

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.caiorodri.agenpet.BuildConfig
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentSobreNosBinding
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController

class SobreNosFragment : Fragment() {

    private var _binding: FragmentSobreNosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSobreNosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val versao = BuildConfig.VERSION_NAME
        binding.txtVersion.text = getString(R.string.sobre_versao, versao)
    }

    private fun setupListeners() {
        binding.btnGithub.setOnClickListener {
            abrirUrl("https://github.com/caiorodri/android-agenpet")
        }

        binding.btnContato.setOnClickListener {
            enviarEmail()
        }

        binding.btnDesenvolvedores.setOnClickListener {
            findNavController().navigate(R.id.action_sobreFragment_to_desenvolvedorFragment)
        }

    }

    private fun abrirUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.erro_abrir_link), Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("agenpet2024@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_assunto_contato))
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.erro_app_email_nao_encontrado), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}