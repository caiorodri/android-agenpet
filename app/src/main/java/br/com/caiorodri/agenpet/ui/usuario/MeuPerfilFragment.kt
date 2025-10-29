package br.com.caiorodri.agenpet.ui.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.activityViewModels;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.databinding.FragmentMeuPerfilBinding;
import br.com.caiorodri.agenpet.mask.DateMaskTextWatcher;
import br.com.caiorodri.agenpet.model.usuario.Endereco;
import br.com.caiorodri.agenpet.model.usuario.Estado;
import br.com.caiorodri.agenpet.model.usuario.Usuario;
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest;
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest;
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel;
import br.com.caiorodri.agenpet.ui.perfil.MeuPerfilViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone

class MeuPerfilFragment : Fragment() {

    private var _binding: FragmentMeuPerfilBinding? = null;
    private val binding get() = _binding!!;

    private val viewModel: MeuPerfilViewModel by viewModels();
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };
    private var usuarioAtual: Usuario? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeuPerfilBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        setupObservers();
        binding.editTextDataNascimento.addTextChangedListener(DateMaskTextWatcher(binding.editTextDataNascimento));
    }

    private fun setupListeners() {
        binding.buttonSalvarPerfil.setOnClickListener {
            validarEsalvar();
        }

        binding.inputLayoutDataNascimento.setEndIconOnClickListener {
            mostrarDatePicker()
        }
        binding.editTextDataNascimento.setOnClickListener {
            mostrarDatePicker()
        }

    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.isVisible = isLoading;
            binding.buttonSalvarPerfil.isEnabled = !isLoading;
        };

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                viewModel.resetError();
            }
        };

        sharedViewModel.usuarioLogado.observe(viewLifecycleOwner) { usuario ->
            if (usuario != null) {
                usuarioAtual = usuario;
                popularCampos(usuario);
            } else {
                Toast.makeText(context, "Não foi possível carregar os dados do perfil.", Toast.LENGTH_SHORT).show();
                findNavController().popBackStack();
            }
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) { usuarioAtualizado ->
            if (usuarioAtualizado != null) {
                Toast.makeText(context, getString(R.string.toast_perfil_atualizado_sucesso), Toast.LENGTH_SHORT).show();
                sharedViewModel.setUsuario(Usuario(usuarioAtualizado));
                viewModel.resetUpdateSuccess();
                findNavController().popBackStack();
            }
        };
    }

    private fun popularCampos(usuario: Usuario) {
        binding.editTextNome.setText(usuario.nome);
        binding.editTextEmail.setText(usuario.email);
        binding.editTextCpf.setText(usuario.cpf);
        binding.editTextTelefone.setText(usuario.telefones?.firstOrNull() ?: "");

        usuario.dataNascimento?.let { data ->
            binding.editTextDataNascimento.setText(dateFormatter.format(Date(data)));
        } ?: binding.editTextDataNascimento.setText("");

        usuario.endereco?.let { end ->
            binding.editTextCep.setText(end.cep);
            binding.editTextLogradouro.setText(end.logradouro);
            binding.editTextNumero.setText(end.numero);
            binding.editTextComplemento.setText(end.complemento ?: "");
            binding.editTextCidade.setText(end.cidade);
            binding.editTextEstado.setText(end.estado?.sigla ?: "");
        };

        binding.inputLayoutCpf.isEnabled = false;
    }

    private fun validarEsalvar() {
        if (!validarCampos()) {
            Toast.makeText(context, "Por favor, corrija os erros no formulário.", Toast.LENGTH_SHORT).show();
            return;
        }

        val usuarioOriginal = usuarioAtual;

        if (usuarioOriginal?.id == null) {
            Toast.makeText(context, "Erro ao obter dados originais do usuário.", Toast.LENGTH_SHORT).show();
            return;
        }

        val nome = binding.editTextNome.text.toString();
        val email = binding.editTextEmail.text.toString();
        val telefone = binding.editTextTelefone.text.toString();
        val dataNascimentoStr = binding.editTextDataNascimento.text.toString();
        val cep = binding.editTextCep.text.toString();
        val logradouro = binding.editTextLogradouro.text.toString();
        val numero = binding.editTextNumero.text.toString();
        val complemento = binding.editTextComplemento.text.toString().takeIf { it.isNotBlank() };
        val cidade = binding.editTextCidade.text.toString();
        val estadoSigla = binding.editTextEstado.text.toString();

        val dataNascimento: Date? = try {
            dateFormatter.isLenient = false;
            dateFormatter.parse(dataNascimentoStr);
        } catch (e: ParseException) {
            binding.inputLayoutDataNascimento.error = "Data inválida";
            return;
        };

        if(dataNascimento == null) {
            binding.inputLayoutDataNascimento.error = "Data não pode ser vazia";
            return;
        }

        val usuarioRequest = UsuarioUpdateRequest(
            id = usuarioOriginal.id,
            nome = nome,
            email = email,
            telefones = listOf(telefone),
            dataNascimento = dataNascimento,
            endereco = Endereco(cep, logradouro, numero, complemento, cidade, Estado(null, estadoSigla)),
            perfil = usuarioOriginal.perfil,
            status = usuarioOriginal.status
        );

        viewModel.salvarAlteracoes(usuarioRequest);
    }

    private fun validarCampos(): Boolean {
        var valido = true;

        binding.inputLayoutNome.error = null;
        binding.inputLayoutEmail.error = null;
        binding.inputLayoutTelefone.error = null;
        binding.inputLayoutDataNascimento.error = null;
        binding.inputLayoutCep.error = null;
        binding.inputLayoutLogradouro.error = null;
        binding.inputLayoutNumero.error = null;
        binding.inputLayoutCidade.error = null;
        binding.inputLayoutEstado.error = null;

        if (binding.editTextNome.text.toString().isBlank()) {
            binding.inputLayoutNome.error = "O nome não pode estar vazio";
            valido = false;
        }

        if (binding.editTextEmail.text.toString().isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches()) {
            binding.inputLayoutEmail.error = "O email precisa ser válido";
            valido = false;
        }

        if (binding.editTextTelefone.text.toString().isBlank()) {
            binding.inputLayoutTelefone.error = "O telefone não pode estar vazio";
            valido = false;
        }

        if (binding.editTextDataNascimento.text.toString().length != 10) {
            binding.inputLayoutDataNascimento.error = "A data deve estar no formato DD/MM/AAAA";
            valido = false;
        } else {
            try {
                dateFormatter.isLenient = false;
                dateFormatter.parse(binding.editTextDataNascimento.text.toString());
            } catch (e: ParseException) {
                binding.inputLayoutDataNascimento.error = "Data inválida";
                valido = false;
            }
        }

        if (binding.editTextCep.text.toString().isBlank()) {
            binding.inputLayoutCep.error = "O CEP não pode estar vazio";
            valido = false;
        }

        if (binding.editTextLogradouro.text.toString().isBlank()) {
            binding.inputLayoutLogradouro.error = "O logradouro não pode estar vazio";
            valido = false;
        }

        if (binding.editTextNumero.text.toString().isBlank()) {
            binding.inputLayoutNumero.error = "O número não pode estar vazio";
            valido = false;
        }

        if (binding.editTextCidade.text.toString().isBlank()) {
            binding.inputLayoutCidade.error = "A cidade não pode estar vazia";
            valido = false;
        }

        if (binding.editTextEstado.text.toString().length != 2) {
            binding.inputLayoutEstado.error = "O estado deve ter 2 caracteres (ex: SP)";
            valido = false;
        }

        return valido;
    }

    private fun mostrarDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder();
        val hojeEmUtc = MaterialDatePicker.todayInUtcMilliseconds();
        val validator = DateValidatorPointBackward.now();

        constraintsBuilder.setEnd(hojeEmUtc);
        constraintsBuilder.setValidator(validator);

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.titulo_datepicker_data_nascimento))
            .setCalendarConstraints(constraintsBuilder.build())
            .build();

        datePicker.addOnPositiveButtonClickListener { dataTimestamp ->
            val data = Date(dataTimestamp);
            binding.editTextDataNascimento.setText(dateFormatter.format(data));
        };

        datePicker.show(childFragmentManager, "DATE_PICKER");
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}