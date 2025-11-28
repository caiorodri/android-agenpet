package br.com.caiorodri.agenpet.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.api.controller.UsuarioController
import br.com.caiorodri.agenpet.model.usuario.LoginRequest
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.security.SessionManager
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    lateinit var usuarioController: UsuarioController;
    lateinit var sessionManager: SessionManager;
    lateinit var buttonLogar: Button;
    lateinit var textViewCadastrar: TextView;
    lateinit var textViewEsqueciSenha: TextView;
    lateinit var editTextEmail: EditText;
    lateinit var editTextSenha: EditText;
    lateinit var frameLayoutLoading: FrameLayout;
    lateinit var checkBoxLembrar: MaterialCheckBox;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        usuarioController = UsuarioController(this);
        sessionManager = SessionManager(this)

        setSetupViews();
        setListeners();

    }

    fun setSetupViews(){

        textViewCadastrar = findViewById(R.id.textViewRealizarCadastro);
        buttonLogar = findViewById(R.id.buttonLogar);
        textViewEsqueciSenha = findViewById(R.id.textViewEsqueciSenha);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        frameLayoutLoading = findViewById(R.id.loadingOverlay);
        checkBoxLembrar = findViewById(R.id.checkbox_lembrar_mim);

    }

    fun setListeners(){

        buttonLogar.setOnClickListener {

            val email = editTextEmail.text.toString();
            val senha = editTextSenha.text.toString();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha e-mail e senha", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            frameLayoutLoading.visibility = FrameLayout.VISIBLE;
            buttonLogar.isEnabled = false;

            lifecycleScope.launch {

                try{

                    val loginRequest = LoginRequest(email, senha);

                    val loginResponse = withContext(Dispatchers.IO) {
                        usuarioController.autenticar(loginRequest);
                    }

                    Log.d("LoginActivity", "Resposta: $loginResponse")

                    if(loginResponse != null){

                        sessionManager.saveAuthToken(loginResponse.token);

                        val deveLembrar = checkBoxLembrar.isChecked;
                        sessionManager.saveRememberMe(deveLembrar);

                        val usuarioResponse = loginResponse.usuario;

                        val intentLoading = Intent(this@LoginActivity, LoadingActivity::class.java);

                        val usuarioLogado = Usuario(usuarioResponse);

                        Log.d("LoginActivity", "Enviando usuário ${usuarioLogado.nome} para LoadingActivity")

                        intentLoading.putExtra("usuarioLogado", usuarioLogado);

                        startActivity(intentLoading);
                        finish();

                    } else {

                        Log.i("LoginActivity", "Usuário ou senha inválidos");
                        Toast.makeText(this@LoginActivity, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();

                    }

                } catch (e: Exception){

                    Toast.makeText(this@LoginActivity, "Falha na comunicação com o servidor.", Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Erro no login: ", e);

                } finally {

                    frameLayoutLoading.visibility = FrameLayout.GONE;
                    buttonLogar.isEnabled = true;

                }


            }


        }

        textViewCadastrar.setOnClickListener {

            val intentCadastrar = Intent(this, CadastroActivity::class.java);
            startActivity(intentCadastrar);

        }

        textViewEsqueciSenha.setOnClickListener {


        }

    }


}