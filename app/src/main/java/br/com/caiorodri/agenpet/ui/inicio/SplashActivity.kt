package br.com.caiorodri.agenpet.ui.inicio

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.security.SessionManager
import br.com.caiorodri.agenpet.settings.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager;

    private var isLoading = true;

    private var proximaIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        settingsManager = SettingsManager(this);
        settingsManager.applyTheme();
        settingsManager.applyLanguage();

        val splashScreen = installSplashScreen();

        super.onCreate(savedInstanceState);

        lifecycleScope.launch {

            definirProximaTela();

            isLoading = false

        }

        splashScreen.setKeepOnScreenCondition { isLoading }

        splashScreen.setOnExitAnimationListener { splashScreenView ->

            val rootView = splashScreenView.view

            val fadeOut = ObjectAnimator.ofFloat(
                rootView,
                View.ALPHA,
                1f,
                0f
            )
            fadeOut.interpolator = AccelerateInterpolator()
            fadeOut.duration = 800L

            fadeOut.doOnEnd {
                splashScreenView.remove()

                if (proximaIntent != null) {

                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.fade_in_suave,
                        0
                    )

                    startActivity(proximaIntent, options.toBundle())

                    finish()
                }
            }

            fadeOut.start()
        }

    }

    private suspend fun definirProximaTela() {
        val sessionManager = SessionManager(applicationContext)
        val token = sessionManager.fetchAuthToken()

        withContext(Dispatchers.IO) {
            delay(1200)
        }

        proximaIntent = if (!token.isNullOrBlank()) {
            Intent(this, LoadingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

    }

}