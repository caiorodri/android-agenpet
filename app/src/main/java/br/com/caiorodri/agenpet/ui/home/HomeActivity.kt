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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.ui.inicio.LoginActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private val sharedViewModel: HomeSharedViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val usuarioRecebido = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("usuarioLogado", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("usuarioLogado")
        }

        if (usuarioRecebido == null) {
            Log.e("HomeActivity", "Usuário não encontrado")
            finish()
            return;
        } else {
            sharedViewModel.setUsuario(usuarioRecebido)
            Log.i("HomeActivity", "Usuário encontrado: ${sharedViewModel.usuarioLogado.value}")
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

                    Toast.makeText(this, "Saindo...", Toast.LENGTH_SHORT).show()
                     val intentLogin = Intent(this, LoginActivity::class.java)
                     startActivity(intentLogin)
                     finish()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

}