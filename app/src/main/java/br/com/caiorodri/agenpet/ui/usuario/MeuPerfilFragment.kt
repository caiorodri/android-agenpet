package br.com.caiorodri.agenpet.ui.usuario;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import br.com.caiorodri.agenpet.security.SessionManager;
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel;
import br.com.caiorodri.agenpet.ui.usuario.MeuPerfilViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import br.com.caiorodri.agenpet.model.usuario.LoginResponse;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.ktx.storage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

class MeuPerfilFragment : Fragment() {

    private var _binding: FragmentMeuPerfilBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var sessionManager: SessionManager;

    private val viewModel: MeuPerfilViewModel by viewModels();
    private val sharedViewModel: HomeSharedViewModel by activityViewModels();

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };
    private var usuarioAtual: Usuario? = null;
    private var listaDeEstados: List<Estado> = emptyList();
    private val storage = Firebase.storage;
    private var fotoUri: Uri? = null;
    private var fotoUrlExistente: String? = null;

    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri == null) {
            return@registerForActivityResult;
        }

        try {
            val pfd = requireContext().contentResolver.openFileDescriptor(uri, "r");
            val tamanhoEmBytes = pfd?.statSize;
            pfd?.close();

            val maxTamanhoEmMB = 30;
            val maxTamanhoEmBytes = maxTamanhoEmMB * 1024 * 1024;

            if (tamanhoEmBytes != null && tamanhoEmBytes > maxTamanhoEmBytes) {
                Toast.makeText(
                    context,
                    getString(R.string.erro_imagem_muito_grande, maxTamanhoEmMB),
                    Toast.LENGTH_LONG
                ).show();

                return@registerForActivityResult;
            }

            binding.progressBarFoto.visibility = View.VISIBLE;
            binding.imageViewFotoPerfil.isEnabled = false;

            fotoUri = uri;

            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.imageViewFotoPerfil);

            binding.progressBarFoto.visibility = View.GONE;
            binding.imageViewFotoPerfil.isEnabled = true;

        } catch (e: Exception) {
            Log.e("MeuPerfilFragment", "Erro ao ler o tamanho do arquivo", e);
            Toast.makeText(context, getString(R.string.erro_selecionar_imagem), Toast.LENGTH_SHORT).show();

            binding.progressBarFoto.visibility = View.GONE;
            binding.imageViewFotoPerfil.isEnabled = true;
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeuPerfilBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager(requireContext());

        setupListeners();
        setupObservers();
        binding.editTextDataNascimento.addTextChangedListener(DateMaskTextWatcher(binding.editTextDataNascimento));
    }

    private fun setupListeners() {
        binding.buttonSalvarPerfil.setOnClickListener {
            validarEsalvar();
        }

        binding.inputLayoutDataNascimento.setEndIconOnClickListener {
            mostrarDatePicker();
        }
        binding.editTextDataNascimento.setOnClickListener {
            mostrarDatePicker();
        }

        binding.imageViewFotoPerfil.setOnClickListener {
            selecionarImagemLauncher.launch("image/*");
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
                Toast.makeText(context, getString(R.string.info_perfil_nao_carregado), Toast.LENGTH_SHORT).show();
                findNavController().popBackStack();
            }
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) { loginResponse ->
            if (loginResponse != null) {
                Toast.makeText(context, getString(R.string.toast_perfil_atualizado_sucesso), Toast.LENGTH_SHORT).show();

                sessionManager.saveAuthToken(loginResponse.token);

                val usuarioAtualizado = Usuario(loginResponse.usuario);

                sharedViewModel.setUsuario(usuarioAtualizado);
                viewModel.resetUpdateSuccess();
                findNavController().popBackStack();
            }
        }

        viewModel.estados.observe(viewLifecycleOwner) { estados ->

            listaDeEstados = estados;
            val siglas = estados.map { it.sigla }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, siglas);
            binding.autoCompleteEstado.setAdapter(adapter);

            usuarioAtual?.endereco?.estado?.sigla?.let {
                binding.autoCompleteEstado.setText(it, false);
            }
        }

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
            binding.autoCompleteEstado.setText(end.estado?.sigla ?: "", false);
        };

        binding.inputLayoutCpf.isEnabled = false;

        fotoUrlExistente = usuario.urlImagem;

        binding.progressBarFoto.visibility = View.VISIBLE;
        binding.imageViewFotoPerfil.isEnabled = false;

        Glide.with(this)
            .load(fotoUrlExistente)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .circleCrop()
            .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                override fun onLoadFailed(e: com.bumptech.glide.load.engine.GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                    binding.progressBarFoto.visibility = View.GONE;
                    binding.imageViewFotoPerfil.isEnabled = true;
                    return false;
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>, dataSource: com.bumptech.glide.load.DataSource, isFirstResource: Boolean): Boolean {
                    binding.progressBarFoto.visibility = View.GONE;
                    binding.imageViewFotoPerfil.isEnabled = true;
                    return false;
                }
            })
            .into(binding.imageViewFotoPerfil);
    }

    private fun validarEsalvar() {
        if (!validarCampos()) {
            Toast.makeText(context, getString(R.string.erro_corrija_formulario), Toast.LENGTH_SHORT).show();
            return;
        }

        val usuarioOriginal = usuarioAtual;

        if (usuarioOriginal?.id == null) {
            Toast.makeText(context, getString(R.string.erro_obter_dados_usuario), Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.setIsLoading(true);

        if (fotoUri != null) {
            comprimirEUploadImagem(fotoUri!!) { downloadUrl ->
                val request = criarUsuarioUpdateRequest(usuarioOriginal, downloadUrl);
                viewModel.salvarAlteracoes(request);
            }
        } else {
            val request = criarUsuarioUpdateRequest(usuarioOriginal, fotoUrlExistente);
            viewModel.salvarAlteracoes(request);
        }

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
            binding.inputLayoutNome.error = getString(R.string.erro_nome_vazio);
            valido = false;
        }

        if (binding.editTextEmail.text.toString().isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches()) {
            binding.inputLayoutEmail.error = getString(R.string.erro_email_invalido);
            valido = false;
        }

        if (binding.editTextTelefone.text.toString().isBlank()) {
            binding.inputLayoutTelefone.error = getString(R.string.erro_telefone_vazio);
            valido = false;
        }

        if (binding.editTextDataNascimento.text.toString().length != 10) {
            binding.inputLayoutDataNascimento.error = getString(R.string.erro_data_formato);
            valido = false;
        } else {
            try {
                dateFormatter.isLenient = false;
                dateFormatter.parse(binding.editTextDataNascimento.text.toString());
            } catch (e: ParseException) {
                binding.inputLayoutDataNascimento.error = getString(R.string.erro_data_invalida);
                valido = false;
            }
        }

        if (binding.editTextCep.text.toString().isBlank()) {
            binding.inputLayoutCep.error = getString(R.string.erro_cep_vazio);
            valido = false;
        }

        if (binding.editTextLogradouro.text.toString().isBlank()) {
            binding.inputLayoutLogradouro.error = getString(R.string.erro_logradouro_vazio);
            valido = false;
        }

        if (binding.editTextNumero.text.toString().isBlank()) {
            binding.inputLayoutNumero.error = getString(R.string.erro_numero_vazio);
            valido = false;
        }

        if (binding.editTextCidade.text.toString().isBlank()) {
            binding.inputLayoutCidade.error = getString(R.string.erro_cidade_vazia);
            valido = false;
        }

        if (binding.autoCompleteEstado.text.toString().isBlank()) {
            binding.inputLayoutEstado.error = getString(R.string.erro_selecionar_estado);
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

    private fun comprimirEUploadImagem(uri: Uri, onSuccess: (String) -> Unit) {
        val MAX_DIMENSION = 1080;

        binding.progressBarFoto.visibility = View.VISIBLE;
        binding.imageViewFotoPerfil.isEnabled = false;

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .override(MAX_DIMENSION, MAX_DIMENSION)
            .centerInside()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    val baos = ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                    val dataEmBytes = baos.toByteArray();

                    Log.d("MeuPerfilFragment", "Imagem original grande, comprimida para ${dataEmBytes.size / 1024} KB");

                    binding.progressBarFoto.visibility = View.GONE;
                    binding.imageViewFotoPerfil.isEnabled = true;

                    uploadBytesParaFirebase(dataEmBytes, onSuccess);
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Log.e("MeuPerfilFragment", "Falha ao carregar/comprimir imagem com Glide");
                    Toast.makeText(context, getString(R.string.erro_processar_imagem), Toast.LENGTH_SHORT).show();
                    viewModel.setIsLoading(false);

                    binding.progressBarFoto.visibility = View.GONE;
                    binding.imageViewFotoPerfil.isEnabled = true;
                }
            })
    }

    private fun uploadBytesParaFirebase(data: ByteArray, onSuccess: (String) -> Unit) {
        val nomeArquivo = "${UUID.randomUUID()}.jpg";
        val ref = storage.reference.child("imagens/usuarios/$nomeArquivo");

        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString());
                }.addOnFailureListener { e ->
                    Log.e("MeuPerfilFragment", "Erro ao obter URL de download", e);
                    Toast.makeText(context, getString(R.string.erro_obter_url, e.message), Toast.LENGTH_SHORT).show();
                    viewModel.setIsLoading(false);
                }
            }
            .addOnFailureListener { e ->
                Log.e("MeuPerfilFragment", "Erro no upload da imagem", e);
                Toast.makeText(context, getString(R.string.erro_upload_imagem, e.message), Toast.LENGTH_SHORT).show();
                viewModel.setIsLoading(false);
            }
    }

    private fun criarUsuarioUpdateRequest(usuarioOriginal: Usuario, urlDaFoto: String?): UsuarioUpdateRequest {

        val nome = binding.editTextNome.text.toString();
        val email = binding.editTextEmail.text.toString();
        val telefone = binding.editTextTelefone.text.toString();
        val dataNascimentoStr = binding.editTextDataNascimento.text.toString();
        val cep = binding.editTextCep.text.toString();
        val logradouro = binding.editTextLogradouro.text.toString();
        val numero = binding.editTextNumero.text.toString();
        val complemento = binding.editTextComplemento.text.toString().takeIf { it.isNotBlank() };
        val cidade = binding.editTextCidade.text.toString();
        val estadoSigla = binding.autoCompleteEstado.text.toString();

        val dataNascimento: Date? = try {
            dateFormatter.isLenient = false;
            dateFormatter.parse(dataNascimentoStr);
        } catch (e: ParseException) {
            null;
        }

        return UsuarioUpdateRequest(
            id = usuarioOriginal.id,
            nome = nome,
            email = email,
            telefones = listOf(telefone),
            dataNascimento = dataNascimento,
            endereco = Endereco(cep, logradouro, numero, complemento, cidade, Estado(null, estadoSigla)),
            perfil = usuarioOriginal.perfil,
            status = usuarioOriginal.status,
            urlImagem = urlDaFoto
        );
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}