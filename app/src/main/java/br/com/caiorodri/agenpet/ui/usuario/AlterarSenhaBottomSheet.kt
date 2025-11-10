package br.com.caiorodri.agenpet.ui.usuario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.LayoutBottomSheetAlterarSenhaBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlterarSenhaBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LayoutBottomSheetAlterarSenhaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlterarSenhaViewModel by viewModels();


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutBottomSheetAlterarSenhaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState);

        setupObservers();

        binding.buttonSalvarNovaSenha.setOnClickListener {

            val senhaAntiga = binding.editTextSenhaAntiga.text.toString();
            val senhaNova = binding.editTextSenhaNova.text.toString();
            val confirmarSenha = binding.editTextConfirmarSenha.text.toString();

            binding.inputLayoutConfirmarSenha.error = null;
            binding.inputLayoutSenhaNova.error = null;
            binding.inputLayoutSenhaAntiga.error = null;

            if (senhaNova != confirmarSenha) {
                binding.inputLayoutConfirmarSenha.error = getString(R.string.erro_senhas_nao_conferem);
                return@setOnClickListener;
            }

            if(senhaNova == senhaAntiga){

                binding.inputLayoutSenhaNova.error = getString(R.string.erro_senha_nova_igual_antiga);
                return@setOnClickListener;

            }

            if(senhaNova.length < 8){

                binding.inputLayoutSenhaNova.error = getString(R.string.erro_senha_minima);
                return@setOnClickListener;

            }


            viewModel.alterarSenha(senhaAntiga, senhaNova);

        }

    }

    private fun setupObservers(){

        viewModel.sucesso.observe(viewLifecycleOwner){ sucesso ->

            if (sucesso == true) {
                Toast.makeText(context, R.string.sucesso_alterar_senha, Toast.LENGTH_SHORT).show();
                viewModel.resetarSucesso();
                dismiss();
            }
        }

        viewModel.erro.observe(viewLifecycleOwner){ erro ->

            if (erro != null) {
                Toast.makeText(context, erro, Toast.LENGTH_SHORT).show();
                viewModel.resetarErro();
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBarSalvar.isVisible = isLoading;

            binding.buttonSalvarNovaSenha.isEnabled = !isLoading;
            binding.buttonSalvarNovaSenha.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE

            binding.editTextSenhaNova.isEnabled = !isLoading;
            binding.editTextSenhaAntiga.isEnabled = !isLoading;
            binding.editTextConfirmarSenha.isEnabled = !isLoading;



        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AlterarSenhaBottomSheet"
    }
}