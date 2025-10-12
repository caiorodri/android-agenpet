package br.com.caiorodri.agenpet.ui.inicio

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.api.controller.UsuarioController
import br.com.caiorodri.agenpet.mask.DateMaskTextWatcher
import br.com.caiorodri.agenpet.model.usuario.Endereco
import br.com.caiorodri.agenpet.model.usuario.Estado
import br.com.caiorodri.agenpet.model.usuario.Perfil
import br.com.caiorodri.agenpet.model.usuario.PerfilEnum
import br.com.caiorodri.agenpet.model.usuario.Status
import br.com.caiorodri.agenpet.model.usuario.StatusEnum
import br.com.caiorodri.agenpet.model.usuario.UsuarioRequest
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class CadastroActivity : AppCompatActivity() {

    private lateinit var usuarioController: UsuarioController;
    private lateinit var constraintLayoutMain: ConstraintLayout;
    private lateinit var frameLayoutLoading: FrameLayout;
    private lateinit var imageViewVoltar: ImageView;
    private lateinit var buttonCadastrar: Button;
    private lateinit var editTextCpf: EditText;
    private lateinit var editTextNome: EditText;
    private lateinit var editTextEmail: EditText;
    private lateinit var editTextDataNascimento: EditText;
    private lateinit var editTextTelefone: EditText;
    private lateinit var editTextSenha: EditText;
    private lateinit var editTextConfirmarSenha: EditText;
    private lateinit var editTextCep: EditText;
    private lateinit var editTextLogradouro: EditText;
    private lateinit var editTextNumero: EditText;
    private lateinit var editTextComplemento: EditText;
    private lateinit var editTextCidade: EditText;
    private lateinit var editTextEstado: EditText;
    private lateinit var imageViewProximo: ImageView;
    private var indiceCampos = 0;
    private var INDICE_DADOS_PESSOAIS = 0;
    private var INDICE_ENDERECO = 1;
    private var INDICE_SENHA = 2;


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioController = UsuarioController();

        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                handleBackPress();

            }

        }

        onBackPressedDispatcher.addCallback(this, callback);

        setUpViews();
        setUpListeners();

    }

    fun setUpViews(){

        constraintLayoutMain = findViewById(R.id.cadastro);
        imageViewVoltar = findViewById(R.id.imageViewVoltar);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        editTextCpf = findViewById(R.id.editTextCpf);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextNome = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDataNascimento = findViewById(R.id.editTextDataNascimento);
        editTextTelefone = findViewById(R.id.editTextTelefone);
        editTextConfirmarSenha = findViewById(R.id.editTextConfirmarSenha);
        imageViewProximo = findViewById(R.id.imageViewProximo);
        editTextCep = findViewById(R.id.editTextCep);
        editTextLogradouro = findViewById(R.id.editTextLogradouro);
        editTextNumero = findViewById(R.id.editTextNumero);
        editTextComplemento = findViewById(R.id.editTextComplemento);
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
        frameLayoutLoading = findViewById(R.id.loadingOverlay);

    }

    fun setUpListeners() {
        imageViewVoltar.setOnClickListener {
            handleBackPress()
        }

        imageViewProximo.setOnClickListener {
            if (indiceCampos < INDICE_SENHA) {
                indiceCampos++
                mudarCampos()
            }
        }

        buttonCadastrar.setOnClickListener {
            if (!validarCadastro()) {
                return@setOnClickListener
            }

            frameLayoutLoading.visibility = FrameLayout.VISIBLE
            buttonCadastrar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val usuarioSalvo = cadastrar()

                    if (usuarioSalvo != null) {
                        Log.i("CadastroActivity", "Usuário ${usuarioSalvo.nome} cadastrado com sucesso")
                        Toast.makeText(this@CadastroActivity, "Usuário ${usuarioSalvo.nome} cadastrado", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CadastroActivity, "Falha ao cadastrar. Verifique os logs.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Log.e("CadastroActivity", "Falha no processo de cadastro: ${e.message}", e)
                    Toast.makeText(this@CadastroActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    frameLayoutLoading.visibility = FrameLayout.GONE
                    buttonCadastrar.isEnabled = true
                }
            }
        }

        editTextDataNascimento.addTextChangedListener(DateMaskTextWatcher(editTextDataNascimento))
    }

    private suspend fun cadastrar(): UsuarioResponse? {

        val nome = editTextNome.text.toString()
        val email = editTextEmail.text.toString()
        val cpf = editTextCpf.text.toString()
        val telefone = editTextTelefone.text.toString()
        val dataNascimentoStr = editTextDataNascimento.text.toString()
        val senha = editTextSenha.text.toString()
        val cep = editTextCep.text.toString()
        val logradouro = editTextLogradouro.text.toString()
        val numero = editTextNumero.text.toString()
        val complemento = editTextComplemento.text.toString()
        val cidade = editTextCidade.text.toString()
        val estado = editTextEstado.text.toString()

        val dataNascimentoFormatada = try {
            SimpleDateFormat("dd/MM/yyyy").parse(dataNascimentoStr)
        } catch (e: Exception) {
            throw Exception("Formato de data inválido. Use DD/MM/AAAA.")
        }

        val usuarioRequest = UsuarioRequest(
            null, nome, email, cpf, senha, listOf(telefone),
            dataNascimentoFormatada,
            Endereco(cep, logradouro, numero, complemento, cidade, Estado(null, estado)), null, null,
            Perfil(PerfilEnum.CLIENTE.getValue(), null), Status(StatusEnum.ATIVO.getValue(), null)
        )

        return withContext(Dispatchers.IO) {
            usuarioController.save(usuarioRequest)
        }
    }

    fun validarCadastro(): Boolean {

        var valido = true;

        if(editTextSenha.text.toString() != editTextConfirmarSenha.text.toString()){

            editTextSenha.error = "As senhas não são iguais";
            editTextConfirmarSenha.error = "As senhas não são iguais";
            valido = false;

        }

        if(editTextSenha.text.toString().length < 8){

            editTextSenha.error = "A senha deve conter no mínimo 8 caracteres";
            editTextConfirmarSenha.error = "A senha deve conter no mínimo 8 caracteres";
            valido = false;

        }

        if(editTextNome.text.toString().isEmpty()){

            editTextNome.error = "O nome não pode estar vazio";
            valido = false;

        }

        if(editTextEmail.text.toString().isEmpty() || !editTextEmail.text.toString().contains("@")){

            editTextEmail.error = "O email precisa ser válido";
            valido = false;

        }

        if(editTextCpf.text.toString().isEmpty() || editTextCpf.text.toString().length != 11){

            editTextCpf.error = "O CPF precisa ser válido";
            valido = false;

        }

        if(editTextTelefone.text.toString().isEmpty()){

            editTextTelefone.error = "O telefone não pode estar vazio";
            valido = false;

        }

        if(editTextDataNascimento.text.toString().isEmpty()){

            editTextDataNascimento.error = "A data de nascimento não pode estar vazia";
            valido = false;

        }

        if(editTextCep.text.toString().isEmpty()){

            editTextCep.error = "O CEP não pode estar vazio";
            valido = false;

        }

        if(editTextLogradouro.text.toString().isEmpty()){

            editTextLogradouro.error = "O logradouro não pode estar vazio";
            valido = false;

        }

        if(editTextNumero.text.toString().isEmpty()){

            editTextNumero.error = "O número não pode estar vazio";
            valido = false;

        }

        if(editTextCidade.text.toString().isEmpty()){

            editTextCidade.error = "A cidade não pode estar vazia";
            valido = false;

        }

        if(editTextEstado.text.toString().isEmpty()){

            editTextEstado.error = "O estado não pode estar vazio";
            valido = false;

        }

        return valido;

    }

    fun mudarCampos() {

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayoutMain)

        fun setDadosPessoaisVisibility(visibility: Int) {
            constraintSet.setVisibility(R.id.editTextNome, visibility)
            constraintSet.setVisibility(R.id.editTextEmail, visibility)
            constraintSet.setVisibility(R.id.editTextCpf, visibility)
            constraintSet.setVisibility(R.id.editTextTelefone, visibility)
            constraintSet.setVisibility(R.id.editTextDataNascimento, visibility)
        }

        fun setEnderecoVisibility(visibility: Int) {
            constraintSet.setVisibility(R.id.editTextCep, visibility)
            constraintSet.setVisibility(R.id.editTextLogradouro, visibility)
            constraintSet.setVisibility(R.id.editTextNumero, visibility)
            constraintSet.setVisibility(R.id.editTextComplemento, visibility)
            constraintSet.setVisibility(R.id.editTextCidade, visibility)
            constraintSet.setVisibility(R.id.editTextEstado, visibility)
        }

        fun setSenhaVisibility(visibility: Int) {
            constraintSet.setVisibility(R.id.editTextSenha, visibility)
            constraintSet.setVisibility(R.id.editTextConfirmarSenha, visibility)
            constraintSet.setVisibility(R.id.buttonCadastrar, visibility)
        }

        when (indiceCampos) {
            INDICE_DADOS_PESSOAIS -> {
                setDadosPessoaisVisibility(ConstraintSet.VISIBLE)
                setEnderecoVisibility(ConstraintSet.GONE)
                setSenhaVisibility(ConstraintSet.GONE)

                constraintSet.setVisibility(R.id.imageViewProximo, ConstraintSet.VISIBLE)
                constraintSet.connect(R.id.imageViewProximo, ConstraintSet.TOP, R.id.editTextDataNascimento, ConstraintSet.BOTTOM, 24)
            }
            INDICE_ENDERECO -> {
                setDadosPessoaisVisibility(ConstraintSet.GONE)
                setEnderecoVisibility(ConstraintSet.VISIBLE)
                setSenhaVisibility(ConstraintSet.GONE)

                constraintSet.setVisibility(R.id.imageViewProximo, ConstraintSet.VISIBLE)
                constraintSet.connect(R.id.imageViewProximo, ConstraintSet.TOP, R.id.editTextEstado, ConstraintSet.BOTTOM, 24)
            }
            INDICE_SENHA -> {
                setDadosPessoaisVisibility(ConstraintSet.GONE)
                setEnderecoVisibility(ConstraintSet.GONE)
                setSenhaVisibility(ConstraintSet.VISIBLE)

                constraintSet.setVisibility(R.id.imageViewProximo, ConstraintSet.GONE)
            }
        }

        TransitionManager.beginDelayedTransition(constraintLayoutMain)
        constraintSet.applyTo(constraintLayoutMain)

    }


    fun handleBackPress(){

        if (indiceCampos > INDICE_DADOS_PESSOAIS) {

            indiceCampos--
            mudarCampos()

        } else {

            finish()

        }

    }

}