package br.com.caiorodri.agenpet.ui.inicio;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.animation.doOnEnd;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.lifecycleScope;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.model.enums.PerfilEnum;
import br.com.caiorodri.agenpet.model.usuario.Usuario;
import br.com.caiorodri.agenpet.security.SessionManager;
import br.com.caiorodri.agenpet.ui.home.ClienteHomeActivity;
import br.com.caiorodri.agenpet.ui.home.HomeActivity;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.async;
import kotlinx.coroutines.delay;
import kotlinx.coroutines.launch;
import kotlinx.coroutines.withContext;

@SuppressLint("CustomSplashScreen")
class LoadingActivity : AppCompatActivity() {

    private lateinit var textViewLoading: TextView;
    private lateinit var usuarioController: UsuarioController;
    private lateinit var sessionManager: SessionManager;

    private lateinit var loadingMessages: List<String>;
    private lateinit var loadingMessagesReduzida: List<String>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_loading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.textViewLoading)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            WindowInsetsCompat.CONSUMED;
        }

        textViewLoading = findViewById(R.id.textViewLoading);
        usuarioController = UsuarioController(this);
        sessionManager = SessionManager(this);

        setupLoadingMessages();
        startLoadingProcess();

    }

    private fun setupLoadingMessages() {
        loadingMessages = listOf(
            getString(R.string.loading_conectando),
            getString(R.string.loading_autenticacao),
            getString(R.string.loading_carregando_perfil),
            getString(R.string.loading_buscando_pets),
            getString(R.string.loading_organizando_agenda),
            getString(R.string.loading_quase_la)
        );

        loadingMessagesReduzida = listOf(
            getString(R.string.loading_conectando),
            getString(R.string.loading_autenticacao)
        );
    }

    private fun startLoadingProcess() {

        lifecycleScope.launch {

            val usuarioLogado: Usuario? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("usuarioLogado", Usuario::class.java);
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("usuarioLogado");
            }

            val finalUsuario: Usuario?;

            if (usuarioLogado != null) {
                val animationJob = launch { runAnimation(loadingMessagesReduzida) }
                animationJob.join();
                finalUsuario = usuarioLogado;

            } else {

                val animationJob = launch {
                    runAnimation(loadingMessages);
                }

                val fetchJob = async {
                    fetchUserData();
                }

                finalUsuario = fetchJob.await();
                animationJob.join();
            }

            if (finalUsuario != null) {

                val partesDoNome = finalUsuario.nome!!.split(" ");
                val primeiroNome = partesDoNome.firstOrNull() ?: "";

                var welcomeMessage = getString(R.string.loading_bem_vindo, primeiroNome);

                val perfilEnum = PerfilEnum.toEnum(finalUsuario.perfil?.id?.toInt());

                if(perfilEnum == PerfilEnum.VETERINARIO && partesDoNome.size > 1){

                    val segundoNome = partesDoNome[1];
                    welcomeMessage = getString(R.string.loading_bem_vindo_composta, primeiroNome, segundoNome);

                }

                runSingleMessageAnimation(welcomeMessage);
            } else {
                delay(500);
            }

            val rootView: View = findViewById(android.R.id.content);

            val fadeOut = ObjectAnimator.ofFloat(rootView, View.ALPHA, 1f, 0f);
            fadeOut.duration = 500L;

            fadeOut.doOnEnd {

                if (finalUsuario != null) {

                    val perfilEnum = PerfilEnum.toEnum(finalUsuario.perfil?.id?.toInt());

                    when (perfilEnum) {
                        PerfilEnum.ADMINISTRADOR,
                        PerfilEnum.VETERINARIO,
                        PerfilEnum.RECEPCIONISTA -> {
                            navigateToHome(finalUsuario);
                        }
                        else -> {
                            navigateToClienteHome(finalUsuario);
                        }
                    }

                } else {
                    navigateToLogin();
                }
            }

            fadeOut.start();
        }
    }

    private suspend fun runAnimation(mensagens: List<String>) {
        val letterDelayMs = 50L;
        val messageDelayMs = 1000L;

        for (mensagem in mensagens) {
            for (i in mensagem.indices) {
                withContext(Dispatchers.Main) {
                    textViewLoading.text = mensagem.substring(0, i + 1) + "_";
                }
                delay(letterDelayMs);
            }

            withContext(Dispatchers.Main) {
                textViewLoading.text = mensagem;
            }

            delay(messageDelayMs);
        }

        delay(300);
    }

    private suspend fun runSingleMessageAnimation(mensagem: String) {
        val letterDelayMs = 50L;
        val messageDelayMs = 1000L;

        for (i in mensagem.indices) {
            withContext(Dispatchers.Main) {
                textViewLoading.text = mensagem.substring(0, i + 1) + "_";
            }
            delay(letterDelayMs);
        }

        withContext(Dispatchers.Main) {
            textViewLoading.text = mensagem;
        }

        delay(messageDelayMs);
    }

    private suspend fun fetchUserData(): Usuario? {

        return withContext(Dispatchers.IO) {

            val usuarioResponse = usuarioController.getMeuPerfil();

            if (usuarioResponse != null) {
                Usuario(usuarioResponse);
            } else {
                sessionManager.clearAuthToken();
                null;
            }

        }

    }

    private fun navigateToClienteHome(usuario: Usuario) {
        val intent = Intent(this, ClienteHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
            putExtra("usuarioLogado", usuario);
        }

        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.fade_in_suave,
            0
        );

        startActivity(intent, options.toBundle());

        finish();
    }

    private fun navigateToHome(usuario: Usuario) {

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
            putExtra("usuarioLogado", usuario);
        }

        val options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in_suave, 0);
        startActivity(intent, options.toBundle());
        finish();
    }

    private fun navigateToLogin() {

        sessionManager.clearAuthToken();
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }

        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.fade_in_suave,
            0
        );

        startActivity(intent, options.toBundle());

        finish();
    }

}