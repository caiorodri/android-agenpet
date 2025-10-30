package br.com.caiorodri.agenpet.ui.inicio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.security.SessionManager
import br.com.caiorodri.agenpet.settings.SettingsManager
import br.com.caiorodri.agenpet.ui.home.HomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen();

        settingsManager = SettingsManager(this);
        settingsManager.applyTheme();
        settingsManager.applyLanguage();

        super.onCreate(savedInstanceState);
        checkLoginStatus();
    }

    private fun checkLoginStatus() {
        val sessionManager = SessionManager(applicationContext);
        val token = sessionManager.fetchAuthToken();

        if (!token.isNullOrBlank()) {
            navigateToHome();
        } else {
            navigateToLogin();
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }
        startActivity(intent);
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }
        startActivity(intent);
    }
}