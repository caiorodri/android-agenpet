package br.com.caiorodri.agenpet.ui.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
    private var toolbarProfileImageView: ImageView? = null;
    private var toolbarProfileProgressBar: View? = null;

    private var profileMenuItem: MenuItem? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_home);

        usuarioController = UsuarioController(this);
        sessionManager = SessionManager(this);

        val usuarioLogadoIntent: Usuario? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("usuarioLogado", Usuario::class.java);
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("usuarioLogado");
        }

        if (usuarioLogadoIntent != null) {
            Log.d("HomeActivity", "Usuário recebido via Intent.");
            sharedViewModel.setUsuario(usuarioLogadoIntent);
        } else {
            Log.e("HomeActivity", "Usuário não recebido via Intent. Buscando perfil na rede.");
            Toast.makeText(this, "Erro ao carregar seu perfil. Tente novamente.", Toast.LENGTH_LONG).show();
            deslogarEVoltarParaLogin();
        }

        Log.d("HomeActivity", "Usuário logado: ${sharedViewModel.usuarioLogado.value}");

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


        bottomNavigationView.setOnItemSelectedListener { item ->
            val options = androidx.navigation.NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.graph.findStartDestination().id, inclusive = false ,saveState = false)
                .build()

            try {
                navController.navigate(item.itemId, null, options)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment ->
                    bottomNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
                R.id.agendamentoFragment ->
                    bottomNavigationView.menu.findItem(R.id.agendamentoFragment).isChecked = true
                R.id.animalFragment ->
                    bottomNavigationView.menu.findItem(R.id.animalFragment).isChecked = true

                R.id.cadastroAnimalFragment ->
                    bottomNavigationView.menu.findItem(R.id.animalFragment).isChecked = true

                 R.id.meuPerfilFragment ->
                     bottomNavigationView.menu.findItem(R.id.homeFragment).isChecked = true

                R.id.agendamentoCadastroFragment ->
                    bottomNavigationView.menu.findItem(R.id.agendamentoFragment).isChecked = true
            }

            when (destination.id) {
                R.id.meuPerfilFragment ->
                    profileMenuItem?.isVisible = false;
                else ->
                    profileMenuItem?.isVisible = true;
            }
        }

        val headerView = navigationView.getHeaderView(0);
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_header_name);
        val userEmailTextView = headerView.findViewById<TextView>(R.id.nav_header_email);
        val headerImageView = headerView.findViewById<ImageView>(R.id.nav_header_image);


        sharedViewModel.usuarioLogado.observe(this) { usuario ->

            userNameTextView.text = usuario.nome;
            userEmailTextView.text = usuario.email;

            carregarImagemPerfilToolbar(usuario.urlImagem);

            Glide.with(this)
                .load(usuario.urlImagem)
                .placeholder(R.drawable.ic_profile_white)
                .error(R.drawable.ic_profile_white)
                .circleCrop()
                .into(headerImageView);

            headerImageView.setOnClickListener {

                navController.navigate(R.id.meuPerfilFragment);

                drawerLayout.closeDrawer(GravityCompat.START);

            }
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

        val profileItem = menu?.findItem(R.id.profile_icon);

        profileMenuItem = profileItem

        val actionView = profileItem?.actionView;

        if (actionView != null) {
            toolbarProfileImageView = actionView.findViewById(R.id.toolbar_profile_image_view);
            toolbarProfileProgressBar = actionView.findViewById(R.id.toolbar_profile_progress);

            actionView.setOnClickListener {
                showProfilePopupMenu(actionView);
            }

            sharedViewModel.usuarioLogado.value?.urlImagem?.let {
                carregarImagemPerfilToolbar(it);
            }
        }

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun showProfilePopupMenu(anchorView: View) {

        val popup = PopupMenu(this, anchorView)

        popup.menuInflater.inflate(R.menu.profile_popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popup_my_profile -> {

                    val navController = findNavController(R.id.fragment_container)
                    navController.navigate(R.id.action_global_meuPerfilFragment)
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
                Toast.makeText(this@HomeActivity, R.string.erro_carregar_usuario, Toast.LENGTH_LONG).show()
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

    private fun carregarImagemPerfilToolbar(url: String?) {
        toolbarProfileImageView?.let { imageView ->

            toolbarProfileProgressBar?.visibility = View.VISIBLE;

            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_profile_white)
                .error(R.drawable.ic_profile_white)
                .circleCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        toolbarProfileProgressBar?.visibility = View.GONE
                        return false;
                    }
                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: com.bumptech.glide.load.DataSource, isFirstResource: Boolean): Boolean {
                        toolbarProfileProgressBar?.visibility = View.GONE
                        return false;
                    }
                })
                .into(imageView);
        }
    }

}