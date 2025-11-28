package br.com.caiorodri.agenpet.ui.usuario;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.navigation.fragment.findNavController;
import androidx.navigation.fragment.navArgs
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.databinding.FragmentFuncionarioCadastroBinding;
import br.com.caiorodri.agenpet.mask.DateMaskTextWatcher;
import br.com.caiorodri.agenpet.model.enums.PerfilEnum
import br.com.caiorodri.agenpet.model.enums.StatusUsuarioEnum
import br.com.caiorodri.agenpet.model.usuario.Endereco;
import br.com.caiorodri.agenpet.model.usuario.Estado;
import br.com.caiorodri.agenpet.model.usuario.Perfil;
import br.com.caiorodri.agenpet.model.usuario.Status;
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest;
import br.com.caiorodri.agenpet.model.usuario.UsuarioUpdateRequest
import br.com.caiorodri.agenpet.ui.usuario.FuncionarioViewModel
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.ktx.storage;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import kotlin.getValue

class FuncionarioCadastroFragment : Fragment() {

    private var _binding: FragmentFuncionarioCadastroBinding? = null;
    private val binding get() = _binding!!;

    private val viewModel: FuncionarioViewModel by viewModels();
    private val storage = Firebase.storage;
    private var fotoUri: Uri? = null;
    private val args: FuncionarioCadastroFragmentArgs by navArgs();
    private var funcionarioParaEdicao: Usuario? = null
    private var urlFotoAtual: String? = null;

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };

    private var listaDeEstados: List<Estado> = listOf(
        Estado("Acre", "AC"), Estado("Alagoas", "AL"), Estado("Amapá", "AP"),
        Estado("Amazonas", "AM"), Estado("Bahia", "BA"), Estado("Ceará", "CE"),
        Estado("Distrito Federal", "DF"), Estado("Espírito Santo", "ES"), Estado("Goiás", "GO"),
        Estado("Maranhão", "MA"), Estado("Mato Grosso", "MT"), Estado("Mato Grosso do Sul", "MS"),
        Estado("Minas Gerais", "MG"), Estado("Pará", "PA"), Estado("Paraíba", "PB"),
        Estado("Paraná", "PR"), Estado("Pernambuco", "PE"), Estado("Piauí", "PI"),
        Estado("Rio de Janeiro", "RJ"), Estado("Rio Grande do Norte", "RN"), Estado("Rio Grande do Sul", "RS"),
        Estado("Rondônia", "RO"), Estado("Roraima", "RR"), Estado("Santa Catarina", "SC"),
        Estado("São Paulo", "SP"), Estado("Sergipe", "SE"), Estado("Tocantins", "TO")
    );

    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUri = uri;
            Glide.with(this).load(uri).circleCrop().into(binding.imageViewFoto);
        }
    };

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFuncionarioCadastroBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        funcionarioParaEdicao = args.funcionario;

        setupDropdowns();
        setupUIBase(funcionarioParaEdicao);
        setupListeners();
        setupObservers();

        binding.editTextDataNascimento.addTextChangedListener(DateMaskTextWatcher(binding.editTextDataNascimento));
    }

    private fun setupDropdowns() {
        val cargos = listOf(
            getString(R.string.cargo_veterinario),
            getString(R.string.cargo_recepcionista)
        );
        val adapterCargo = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cargos);
        binding.autoCompleteCargo.setAdapter(adapterCargo);

        val siglas = listaDeEstados.map { it.sigla };
        val adapterEstado = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, siglas);
        binding.autoCompleteEstado.setAdapter(adapterEstado);
    }

    private fun setupUIBase(funcionario: Usuario?) {

        if (funcionario != null) {

            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_editar_funcionario);
            binding.buttonSalvar.text = getString(R.string.botao_atualizar_funcionario);

            binding.editTextSenha.visibility = View.GONE;
            binding.inputLayoutSenha.visibility = View.GONE;

            binding.editTextNome.setText(funcionario.nome);
            binding.editTextEmail.setText(funcionario.email);
            binding.editTextCpf.setText(funcionario.cpf);


            val cargoTexto = if (funcionario.perfil?.id == PerfilEnum.VETERINARIO.id) getString(R.string.cargo_veterinario) else getString(R.string.cargo_recepcionista);
            binding.autoCompleteCargo.setText(cargoTexto, false);

            if (!funcionario.telefones.isNullOrEmpty()) {
                binding.editTextTelefone.setText(funcionario.telefones[0]);
            }

            funcionario.dataNascimento?.let {
                binding.editTextDataNascimento.setText(dateFormatter.format(Date(it)));
            }

            funcionario.endereco?.let { end ->
                binding.editTextCep.setText(end.cep);
                binding.editTextLogradouro.setText(end.logradouro);
                binding.editTextNumero.setText(end.numero);
                binding.editTextComplemento.setText(end.complemento);
                binding.editTextCidade.setText(end.cidade);
                binding.autoCompleteEstado.setText(end.estado?.sigla, false);
            }

            urlFotoAtual = funcionario.urlImagem;
            if (urlFotoAtual != null) {
                Glide.with(this).load(urlFotoAtual).circleCrop().into(binding.imageViewFoto);
            }

        } else {
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.botao_cadastrar_funcionario);
            binding.buttonSalvar.text = getString(R.string.botao_cadastrar_funcionario);

            binding.inputLayoutSenha.visibility = View.VISIBLE;
            binding.editTextSenha.visibility = View.VISIBLE;

        }
    }

    private fun setupListeners() {
        binding.buttonSalvar.setOnClickListener {
            validarESalvar();
        };

        binding.imageViewFoto.setOnClickListener {
            selecionarImagemLauncher.launch("image/*");
        }

        binding.inputLayoutDataNascimento.setEndIconOnClickListener { mostrarDatePicker() }
        binding.editTextDataNascimento.setOnClickListener { mostrarDatePicker() }
    }

    private fun setupObservers() {

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->

            binding.loadingOverlay.isVisible = loading;
            binding.buttonSalvar.isEnabled = !loading;

        }

        viewModel.sucessoCadastro.observe(viewLifecycleOwner) { sucesso ->

            if (sucesso) {

                val msg = if (funcionarioParaEdicao != null) {
                    getString(R.string.sucesso_funcionario_atualizado)
                } else {
                    getString(R.string.sucesso_funcionario_salvo)
                }

                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                viewModel.resetSucesso()
                findNavController().popBackStack()

            }
        }

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            if (erro != null) {
                Toast.makeText(context, erro, Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun mostrarDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

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

    private fun validarESalvar() {
        val nome = binding.editTextNome.text.toString();
        val email = binding.editTextEmail.text.toString();
        val cpf = binding.editTextCpf.text.toString();
        val senha = binding.editTextSenha.text.toString();
        val cargoSelecionado = binding.autoCompleteCargo.text.toString();
        val telefone = binding.editTextTelefone.text.toString();
        val dataNascStr = binding.editTextDataNascimento.text.toString();
        val cep = binding.editTextCep.text.toString();
        val logradouro = binding.editTextLogradouro.text.toString();
        val numero = binding.editTextNumero.text.toString();
        val cidade = binding.editTextCidade.text.toString();
        val estado = binding.autoCompleteEstado.text.toString();

        var valido = true;

        binding.inputLayoutNome.error = null;
        binding.inputLayoutEmail.error = null;
        binding.inputLayoutCpf.error = null;
        binding.inputLayoutSenha.error = null;
        binding.inputLayoutCargo.error = null;
        binding.inputLayoutTelefone.error = null;
        binding.inputLayoutDataNascimento.error = null;
        binding.inputLayoutCep.error = null;
        binding.inputLayoutLogradouro.error = null;
        binding.inputLayoutNumero.error = null;
        binding.inputLayoutCidade.error = null;
        binding.inputLayoutEstado.error = null;

        if (nome.isBlank()) { binding.inputLayoutNome.error = getString(R.string.erro_obrigatorio); valido = false; }
        if (email.isBlank()) { binding.inputLayoutEmail.error = getString(R.string.erro_obrigatorio); valido = false; }
        if (cpf.isBlank()) { binding.inputLayoutCpf.error = getString(R.string.erro_obrigatorio); valido = false; }

        if (funcionarioParaEdicao == null && senha.isBlank()) {
            binding.inputLayoutSenha.error = getString(R.string.erro_obrigatorio);
            valido = false;
        }

        if (cargoSelecionado.isBlank()) { binding.inputLayoutCargo.error = getString(R.string.erro_cargo_vazio); valido = false; }
        if (telefone.isBlank()) { binding.inputLayoutTelefone.error = getString(R.string.erro_obrigatorio); valido = false; }

        var dataNascimento: Date? = null;
        if (dataNascStr.isBlank()) {
            binding.inputLayoutDataNascimento.error = getString(R.string.erro_obrigatorio); valido = false;
        } else {
            try {
                dateFormatter.isLenient = false;
                dataNascimento = dateFormatter.parse(dataNascStr);
            } catch (e: Exception) {
                binding.inputLayoutDataNascimento.error = getString(R.string.erro_data_invalida); valido = false;
            }
        }

        if (cep.isBlank()) { binding.inputLayoutCep.error = getString(R.string.erro_obrigatorio); valido = false; }
        if (logradouro.isBlank()) { binding.inputLayoutLogradouro.error = getString(R.string.erro_obrigatorio); valido = false; }
        if (numero.isBlank()) { binding.inputLayoutNumero.error = getString(R.string.erro_obrigatorio); valido = false; }
        if (cidade.isBlank()) { binding.inputLayoutCidade.error = getString(R.string.erro_obrigatorio); valido = false; }
        if (estado.isBlank()) { binding.inputLayoutEstado.error = getString(R.string.erro_obrigatorio); valido = false; }

        if (!valido) return;

        val perfilId: Int = if (cargoSelecionado == getString(R.string.cargo_veterinario)) PerfilEnum.VETERINARIO.id else PerfilEnum.RECEPCIONISTA.id;

        val nomePerfil = if(perfilId == PerfilEnum.VETERINARIO.id) PerfilEnum.VETERINARIO.nome else PerfilEnum.RECEPCIONISTA.nome;

        if (fotoUri != null) {
            comprimirEUploadImagem(fotoUri!!) { urlFoto ->
                processarSalvarOuAtualizar(
                    nome, email, cpf, senha, telefone, dataNascimento,
                    cep, logradouro, numero, cidade, estado,
                    perfilId, nomePerfil, urlFoto
                )
            }
        } else {
            val urlParaSalvar = if (funcionarioParaEdicao != null) urlFotoAtual else null;

            processarSalvarOuAtualizar(
                nome, email, cpf, senha, telefone, dataNascimento,
                cep, logradouro, numero, cidade, estado,
                perfilId, nomePerfil, urlParaSalvar
            )
        }
    }

    private fun processarSalvarOuAtualizar(
        nome: String, email: String, cpf: String, senha: String, telefone: String, dataNascimento: Date?,
        cep: String, logradouro: String, numero: String, cidade: String, estado: String,
        perfilId: Int, nomePerfil: String, urlFoto: String?
    ) {
        val complemento = binding.editTextComplemento.text.toString();

        if (funcionarioParaEdicao == null) {
            val novoFuncionario = UsuarioRequest(
                id = null,
                nome = nome,
                email = email,
                cpf = cpf,
                senha = senha,
                telefones = listOf(telefone),
                dataNascimento = dataNascimento,
                endereco = Endereco(cep, logradouro, numero, complemento, cidade, Estado(null, estado)),
                perfil = Perfil(perfilId, nomePerfil),
                status = Status(StatusUsuarioEnum.ATIVO.id, StatusUsuarioEnum.ATIVO.nome),
                urlImagem = urlFoto,
                agendamentos = null,
                animais = null,
                receberEmail = true
            );
            viewModel.salvarFuncionario(novoFuncionario);

        } else {

            val funcionarioAtualizado = UsuarioUpdateRequest(
                id = funcionarioParaEdicao!!.id,
                nome = nome,
                email = email,
                telefones = listOf(telefone),
                dataNascimento = dataNascimento,
                endereco = Endereco(
                    cep,
                    logradouro,
                    numero,
                    complemento,
                    cidade,
                    Estado(null, estado)
                ),
                perfil = Perfil(perfilId, nomePerfil),
                status = funcionarioParaEdicao!!.status,
                urlImagem = urlFoto,
            );
            viewModel.atualizarFuncionario(funcionarioAtualizado);
        }
    }

    private fun comprimirEUploadImagem(uri: Uri, onSuccess: (String) -> Unit) {
        binding.loadingOverlay.isVisible = true;

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .override(1080, 1080)
            .centerInside()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val baos = ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                    val data = baos.toByteArray();
                    uploadBytesParaFirebase(data, onSuccess);
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Toast.makeText(context, getString(R.string.erro_processar_imagem), Toast.LENGTH_SHORT).show();
                    binding.loadingOverlay.isVisible = false;
                }
            });
    }

    private fun uploadBytesParaFirebase(data: ByteArray, onSuccess: (String) -> Unit) {
        val ref = storage.reference.child("imagens/usuarios/${UUID.randomUUID()}.jpg");
        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri -> onSuccess(uri.toString()) }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro no upload da imagem", Toast.LENGTH_SHORT).show();
                binding.loadingOverlay.isVisible = false;
            }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}