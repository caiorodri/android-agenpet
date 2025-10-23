package br.com.caiorodri.agenpet.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.api.controller.UsuarioController
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.security.SessionManager
import br.com.caiorodri.agenpet.ui.inicio.LoginActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private val sharedViewModel: HomeSharedViewModel by viewModels();
    private lateinit var drawerLayout: DrawerLayout;
    private lateinit var appBarConfiguration: AppBarConfiguration;
    private lateinit var usuarioController: UsuarioController;
    private lateinit var sessionManager: SessionManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_home);

        usuarioController = UsuarioController(this);
        sessionManager = SessionManager(this);

        val usuarioLogadoIntent: Usuario? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("usuarioLogado", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("usuarioLogado")
        }

        if (usuarioLogadoIntent != null) {
            Log.d("HomeActivity", "Usuário recebido via Intent.")
            sharedViewModel.setUsuario(usuarioLogadoIntent)
        } else {
            Log.d("HomeActivity", "Usuário não recebido via Intent. Buscando perfil na rede.")
            carregarDadosDoUsuario()
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        val navigationView: NavigationView = findViewById(R.id.navigation_view);
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation);
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.agendamentoFragment, R.id.animalFragment),
            drawerLayout
        );

        setupActionBarWithNavController(navController, appBarConfiguration);
        navigationView.setupWithNavController(navController);
        bottomNavigationView.setupWithNavController(navController);

        val headerView = navigationView.getHeaderView(0);
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_header_name);
        val userEmailTextView = headerView.findViewById<TextView>(R.id.nav_header_email);

        sharedViewModel.usuarioLogado.observe(this) { usuario ->

            userNameTextView.text = usuario.nome;
            userEmailTextView.text = usuario.email;

        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.profile_icon) {
            val anchorView = findViewById<View>(R.id.profile_icon)
            showProfilePopupMenu(anchorView)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showProfilePopupMenu(anchorView: View) {

        val popup = PopupMenu(this, anchorView)

        popup.menuInflater.inflate(R.menu.profile_popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popup_my_profile -> {
                    Toast.makeText(this, "Abrindo Meu Perfil...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.popup_logout -> {

                    deslogarEVoltarParaLogin();
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    fun carregarDadosDoUsuario() {

        sharedViewModel.setLoading(true);

        lifecycleScope.launch {

            val usuarioResponse = withContext(Dispatchers.IO) {
                usuarioController.getMeuPerfil();
            }

            if (usuarioResponse != null) {
                val usuarioLogado = Usuario(usuarioResponse);
                sharedViewModel.setUsuario(usuarioLogado);
            } else {
                Toast.makeText(this@HomeActivity, "Sua sessão expirou. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
                deslogarEVoltarParaLogin();
            }

            sharedViewModel.setLoading(false);

        }
    }

    private fun deslogarEVoltarParaLogin() {
        sessionManager.clearAuthToken()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

}