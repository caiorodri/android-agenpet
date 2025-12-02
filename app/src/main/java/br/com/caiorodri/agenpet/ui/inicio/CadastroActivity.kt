package br.com.caiorodri.agenpet.ui.inicio;

import android.os.Bundle;
import android.util.Log;
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.lifecycleScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.api.controller.ViaCepController
import br.com.caiorodri.agenpet.mask.DateMaskTextWatcher;
import br.com.caiorodri.agenpet.model.enums.EstadoEnum
import br.com.caiorodri.agenpet.model.enums.PerfilEnum
import br.com.caiorodri.agenpet.model.enums.StatusUsuarioEnum
import br.com.caiorodri.agenpet.model.usuario.Endereco;
import br.com.caiorodri.agenpet.model.usuario.Estado;
import br.com.caiorodri.agenpet.model.usuario.Perfil;
import br.com.caiorodri.agenpet.model.usuario.Status;
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest;
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse;
import br.com.caiorodri.agenpet.utils.MaskTextWatcher
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;
import kotlinx.coroutines.withContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class CadastroActivity : AppCompatActivity() {

    private lateinit var usuarioController: UsuarioController;
    private val viaCepController = ViaCepController()
    private lateinit var frameLayoutLoading: FrameLayout;
    private lateinit var buttonCadastrar: Button;
    private lateinit var toolbar: MaterialToolbar;

    private lateinit var editTextNome: TextInputEditText;
    private lateinit var editTextEmail: TextInputEditText;
    private lateinit var editTextCpf: TextInputEditText;
    private lateinit var editTextDataNascimento: TextInputEditText;
    private lateinit var editTextTelefone: TextInputEditText;
    private lateinit var editTextSenha: TextInputEditText;
    private lateinit var editTextConfirmarSenha: TextInputEditText;
    private lateinit var editTextCep: TextInputEditText;
    private lateinit var editTextLogradouro: TextInputEditText;
    private lateinit var editTextNumero: TextInputEditText;
    private lateinit var editTextComplemento: TextInputEditText;
    private lateinit var editTextCidade: TextInputEditText;
    private lateinit var autoCompleteEstado: AutoCompleteTextView;

    private lateinit var inputLayoutNome: TextInputLayout;
    private lateinit var inputLayoutEmail: TextInputLayout;
    private lateinit var inputLayoutCpf: TextInputLayout;
    private lateinit var inputLayoutDataNascimento: TextInputLayout;
    private lateinit var inputLayoutTelefone: TextInputLayout;
    private lateinit var inputLayoutSenha: TextInputLayout;
    private lateinit var inputLayoutConfirmarSenha: TextInputLayout;
    private lateinit var inputLayoutCep: TextInputLayout;
    private lateinit var inputLayoutLogradouro: TextInputLayout;
    private lateinit var inputLayoutNumero: TextInputLayout;
    private lateinit var inputLayoutCidade: TextInputLayout;
    private lateinit var inputLayoutEstado: TextInputLayout;
    private lateinit var checkboxReceberEmail: MaterialCheckBox;

    private val formatadorDeData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastro_root)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom

            view.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                bottomPadding
            )

            insets
        }

        usuarioController = UsuarioController(this);

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);

        setUpViews();
        setUpListeners();
        carregarEstados();

    };

    fun setUpViews() {
        frameLayoutLoading = findViewById(R.id.loadingOverlay);
        buttonCadastrar = findViewById(R.id.button_cadastrar);
        toolbar = findViewById(R.id.toolbar_cadastro);

        editTextNome = findViewById(R.id.edit_text_nome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCpf = findViewById(R.id.edit_text_cpf);
        editTextDataNascimento = findViewById(R.id.edit_text_data_nascimento);
        editTextTelefone = findViewById(R.id.edit_text_telefone);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextConfirmarSenha = findViewById(R.id.edit_text_confirmar_senha);
        editTextCep = findViewById(R.id.edit_text_cep);
        editTextLogradouro = findViewById(R.id.edit_text_logradouro);
        editTextNumero = findViewById(R.id.edit_text_numero);
        editTextComplemento = findViewById(R.id.edit_text_complemento);
        editTextCidade = findViewById(R.id.edit_text_cidade);
        autoCompleteEstado = findViewById(R.id.auto_complete_estado);

        inputLayoutNome = findViewById(R.id.input_layout_nome);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutCpf = findViewById(R.id.input_layout_cpf);
        inputLayoutDataNascimento = findViewById(R.id.input_layout_data_nascimento);
        inputLayoutTelefone = findViewById(R.id.input_layout_telefone);
        inputLayoutSenha = findViewById(R.id.input_layout_senha);
        inputLayoutConfirmarSenha = findViewById(R.id.input_layout_confirmar_senha);
        inputLayoutCep = findViewById(R.id.input_layout_cep);
        inputLayoutLogradouro = findViewById(R.id.input_layout_logradouro);
        inputLayoutNumero = findViewById(R.id.input_layout_numero);
        inputLayoutCidade = findViewById(R.id.input_layout_cidade);
        inputLayoutEstado = findViewById(R.id.input_layout_estado);

        checkboxReceberEmail = findViewById(R.id.checkbox_receber_email);
    };

    fun setUpListeners() {

        toolbar.setNavigationOnClickListener {
            finish();
        };

        buttonCadastrar.setOnClickListener {
            if (!validarCadastro()) {
                Toast.makeText(this@CadastroActivity, getString(R.string.erro_corrija_formulario), Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            frameLayoutLoading.visibility = FrameLayout.VISIBLE;
            buttonCadastrar.isEnabled = false;

            lifecycleScope.launch {
                try {
                    val usuarioSalvo = cadastrar();

                    if (usuarioSalvo != null) {
                        Log.i("CadastroActivity", "Usuário ${usuarioSalvo.nome} cadastrado com sucesso");
                        Toast.makeText(this@CadastroActivity, getString(R.string.sucesso_cadastro_usuario, usuarioSalvo.nome), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this@CadastroActivity, getString(R.string.erro_falha_cadastro), Toast.LENGTH_LONG).show();
                    }

                } catch (e: Exception) {
                    Log.e("CadastroActivity", "Falha no processo de cadastro: ${e.message}", e);
                    Toast.makeText(this@CadastroActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show();
                } finally {
                    frameLayoutLoading.visibility = FrameLayout.GONE;
                    buttonCadastrar.isEnabled = true;
                }
            }
        }

        editTextDataNascimento.addTextChangedListener(DateMaskTextWatcher(editTextDataNascimento));

        inputLayoutDataNascimento.setEndIconOnClickListener {
            mostrarDatePicker();
        }
        editTextDataNascimento.setOnClickListener {
            mostrarDatePicker();
        }

        editTextTelefone.addTextChangedListener(
            MaskTextWatcher(
                editTextTelefone,
                "(##) #####-####"
            )
        )
        editTextCep.addTextChangedListener(MaskTextWatcher(editTextCep, "#####-###"));
        editTextCpf.addTextChangedListener(MaskTextWatcher(editTextCpf, "###.###.###-##"));

        editTextCep.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                val cep = MaskTextWatcher.unmask(editTextCep.text.toString());
                if (cep.length == 8) {
                    buscarCep(cep);
                }
            }
        }

    }

    private fun carregarEstados() {

        inputLayoutEstado.isEnabled = false;

        lifecycleScope.launch {
            try {

                val siglasDosEstados = EstadoEnum.entries.map { it.sigla };

                val adapter = ArrayAdapter(this@CadastroActivity, android.R.layout.simple_dropdown_item_1line, siglasDosEstados);
                autoCompleteEstado.setAdapter(adapter);

                inputLayoutEstado.isEnabled = true;

            } catch (e: Exception) {
                Log.e("CadastroActivity", "Erro ao carregar estados", e);
                inputLayoutEstado.error = getString(R.string.erro_carregar_lista);
                Toast.makeText(this@CadastroActivity, "Falha ao carregar estados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun buscarCep(cep: String) {

        inputLayoutLogradouro.isEnabled = false;
        inputLayoutCidade.isEnabled = false;
        inputLayoutEstado.isEnabled = false;

        lifecycleScope.launch {

            val endereco = withContext(Dispatchers.IO) {
                viaCepController.buscarCep(cep);
            }

            if (endereco != null) {
                editTextLogradouro.setText(endereco.logradouro);
                editTextCidade.setText(endereco.localidade);
                editTextComplemento.setText(endereco.complemento);

                autoCompleteEstado.setText(endereco.uf, false);

                editTextNumero.requestFocus();

            } else {

                Toast.makeText(this@CadastroActivity, "CEP não encontrado", Toast.LENGTH_SHORT).show();

            }

            inputLayoutLogradouro.isEnabled = true;
            inputLayoutCidade.isEnabled = true;
            inputLayoutEstado.isEnabled = true;
        }
    }

    private suspend fun cadastrar(): UsuarioResponse? {

        val nome = editTextNome.text.toString();
        val email = editTextEmail.text.toString();
        val cpf = MaskTextWatcher.unmask(editTextCpf.text.toString());

        val telefoneComMascara = editTextTelefone.text.toString();
        val telefone = MaskTextWatcher.unmask(telefoneComMascara);

        val dataNascimentoStr = editTextDataNascimento.text.toString();
        val senha = editTextSenha.text.toString();

        val cep = MaskTextWatcher.unmask(editTextCep.text.toString());

        val logradouro = editTextLogradouro.text.toString();
        val numero = editTextNumero.text.toString();
        val complemento = editTextComplemento.text.toString();
        val cidade = editTextCidade.text.toString();
        val siglaEstado = autoCompleteEstado.text.toString();
        val receberEmail = checkboxReceberEmail.isChecked;

        val dataNascimentoFormatada = try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataNascimentoStr);
        } catch (e: Exception) {
            throw Exception(getString(R.string.erro_formato_data_invalido));
        }

        val perfilCliente = PerfilEnum.CLIENTE;
        val statusAtivo = StatusUsuarioEnum.ATIVO;

        val usuarioRequest = UsuarioRequest(
            null, nome, email, cpf, senha, listOf(telefone),
            dataNascimentoFormatada,
            Endereco(cep, logradouro, numero, complemento, cidade, Estado(null, siglaEstado)), null, null,
            Perfil(perfilCliente.id, perfilCliente.nome), Status(statusAtivo.id, statusAtivo.nome), null, receberEmail
        );

        return withContext(Dispatchers.IO) {
            usuarioController.salvar(usuarioRequest);
        }
    }

    fun validarCadastro(): Boolean {

        var valido = true;

        inputLayoutNome.error = null;
        inputLayoutEmail.error = null;
        inputLayoutCpf.error = null;
        inputLayoutTelefone.error = null;
        inputLayoutDataNascimento.error = null;
        inputLayoutCep.error = null;
        inputLayoutLogradouro.error = null;
        inputLayoutNumero.error = null;
        inputLayoutCidade.error = null;
        inputLayoutEstado.error = null;
        inputLayoutSenha.error = null;
        inputLayoutConfirmarSenha.error = null;

        if (editTextSenha.text.toString().length < 8) {
            inputLayoutSenha.error = getString(R.string.erro_senha_minima);
            valido = false;
        }

        if (editTextSenha.text.toString() != editTextConfirmarSenha.text.toString()) {
            inputLayoutConfirmarSenha.error = getString(R.string.erro_senhas_nao_conferem);
            valido = false;
        }

        if (editTextNome.text.toString().isEmpty()) {
            inputLayoutNome.error = getString(R.string.erro_nome_vazio);
            valido = false;
        }

        if (editTextEmail.text.toString().isEmpty() || !editTextEmail.text.toString().contains("@")) {
            inputLayoutEmail.error = getString(R.string.erro_email_invalido);
            valido = false;
        }

        if (editTextCpf.text.toString().length != 11) {
            inputLayoutCpf.error = getString(R.string.erro_cpf_formato);
            valido = false;
        }

        if (editTextTelefone.text.toString().isEmpty()) {
            inputLayoutTelefone.error = getString(R.string.erro_telefone_vazio);
            valido = false;
        }

        if (editTextDataNascimento.text.toString().length != 10) {
            inputLayoutDataNascimento.error = getString(R.string.erro_data_formato);
            valido = false;
        }

        if (editTextCep.text.toString().isEmpty()) {
            inputLayoutCep.error = getString(R.string.erro_cep_vazio);
            valido = false;
        }

        if (editTextLogradouro.text.toString().isEmpty()) {
            inputLayoutLogradouro.error = getString(R.string.erro_logradouro_vazio);
            valido = false;
        }

        if (editTextNumero.text.toString().isEmpty()) {
            inputLayoutNumero.error = getString(R.string.erro_numero_vazio);
            valido = false;
        }

        if (editTextCidade.text.toString().isEmpty()) {
            inputLayoutCidade.error = getString(R.string.erro_cidade_vazia);
            valido = false;
        }

        if (autoCompleteEstado.text.toString().isBlank()) {
            inputLayoutEstado.error = getString(R.string.erro_selecionar_estado);
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
            editTextDataNascimento.setText(formatadorDeData.format(data));
        };

        datePicker.show(supportFragmentManager, "DATE_PICKER");
    }
}