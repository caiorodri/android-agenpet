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
import br.com.caiorodri.agenpet.model.agendamento.Agendamento
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.ui.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {

    lateinit var usuarioController: UsuarioController;
    lateinit var buttonLogar: Button;
    lateinit var textViewCadastrar: TextView;
    lateinit var textViewEsqueciSenha: TextView;
    lateinit var editTextEmail: EditText;
    lateinit var editTextSenha: EditText;
    lateinit var frameLayoutLoading: FrameLayout;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioController = UsuarioController();
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

    }

    fun setListeners(){

        buttonLogar.setOnClickListener {

            val email = editTextEmail.text.toString();
            val senha = editTextSenha.text.toString();

            frameLayoutLoading.visibility = FrameLayout.VISIBLE;
            buttonLogar.isEnabled = false;

            lifecycleScope.launch {

                try{

                    val request: UsuarioResponse? = withContext(Dispatchers.IO){
                        usuarioController.findByEmailAndSenha(email, senha)
                    };

                    Log.d("LoginActivity", "Resposta: $request")

                    if(request != null){

                        Toast.makeText(this@LoginActivity, "Usuário ${request.nome} logado com sucesso", Toast.LENGTH_SHORT).show();

                        val intentHome = Intent(this@LoginActivity, HomeActivity::class.java);

                        val usuarioLogado = Usuario(request);

                        val listaDeAgendamentos = mutableListOf<Agendamento>();
                        val listaDeAnimais = mutableListOf<Animal>();

                        for(agendamentoResponse in request.agendamentos){

                            Log.d("LoginActivity", "Agendamento: $agendamentoResponse")
                            listaDeAgendamentos += Agendamento(agendamentoResponse);

                        }

                        usuarioLogado.agendamentos = listaDeAgendamentos;

                        for(animalResponse in request.animais){

                            Log.d("LoginActivity", "Animal: $animalResponse")
                            listaDeAnimais += Animal(animalResponse);

                        }

                        usuarioLogado.animais = listaDeAnimais;

                        Log.d("LoginActivity", "Usuário logado: $usuarioLogado")

                        intentHome.putExtra("usuarioLogado", usuarioLogado);

                        startActivity(intentHome);
                        finish();

                    } else {

                        Log.i("LoginActivity", "Usuário ou senha inválidos")
                        Toast.makeText(this@LoginActivity, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();

                    }

                } catch (e: Exception){

                    Log.e("LoginActivity", "${e.message}")
                    Toast.makeText(this@LoginActivity, "${e.message}", Toast.LENGTH_SHORT).show();

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