package br.com.caiorodri.agenpet.ui.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource

class HomeActivity : AppCompatActivity() {

    private val sharedViewModel: HomeSharedViewModel by viewModels();
    private lateinit var drawerLayout: DrawerLayout;
    private lateinit var appBarConfiguration: AppBarConfiguration;
    private lateinit var usuarioController: UsuarioController;
    private lateinit var sessionManager: SessionManager;
    private var toolbarProfileImageView: ImageView? = null;
    private var toolbarProfileProgressBar: View? = null;
    private lateinit var bottomNavigationView: BottomNavigationView;
    private lateinit var navigationView: NavigationView;
    private lateinit var navController: NavController;
    private var profileMenuItem: MenuItem? = null;


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_home);

        usuarioController = UsuarioController(this);
        sessionManager = SessionManager(this);

        setupViews();
        processarIntentUsuario();
        setupNavigation();
        setupObservers();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom

            view.setPadding(systemBars.left, 0, systemBars.right, bottomPadding)

            insets
        }

    }

    private fun setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
    }

    private fun processarIntentUsuario() {

        val usuarioLogadoIntent: Usuario? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            intent.getParcelableExtra("usuarioLogado", Usuario::class.java);

        } else {

            @Suppress("DEPRECATION")
            intent.getParcelableExtra("usuarioLogado");

        }

        if (usuarioLogadoIntent != null) {

            sharedViewModel.setUsuario(usuarioLogadoIntent);

        } else {

            carregarDadosDoUsuario();

        }

    }

    private fun setupNavigation() {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment;
        navController = navHostFragment.navController;
        val bottomAppBar = findViewById<View>(R.id.bottomAppBar);
        val fragmentContainer = findViewById<View>(R.id.fragment_container)

        val paddingComMenu = (80 * resources.displayMetrics.density).toInt()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeProfissionalFragment,
                R.id.agendamentoFragment,
                R.id.funcionarioFragment
            ),
            drawerLayout
        );

        setupActionBarWithNavController(navController, appBarConfiguration);

        bottomNavigationView.setOnItemSelectedListener { item ->
            try {
                navController.navigate(item.itemId);
                true;
            } catch (e: Exception) {
                false;
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val deveMostrarMenu = when (destination.id) {
                R.id.homeProfissionalFragment,
                R.id.agendamentoFragment,
                R.id.funcionarioFragment -> true

                else -> false
            }

            if (deveMostrarMenu) {

                bottomNavigationView.visibility = View.VISIBLE;
                bottomAppBar.visibility = View.VISIBLE;

                fragmentContainer.setPadding(0, 0, 0, paddingComMenu);

                if (bottomNavigationView.menu.findItem(destination.id) != null) {
                    bottomNavigationView.menu.findItem(destination.id).isChecked = true;
                }

            } else {

                bottomNavigationView.visibility = View.GONE;
                bottomAppBar.visibility = View.GONE;
                fragmentContainer.setPadding(0, 0, 0, 0)

            }

            profileMenuItem?.isVisible = (destination.id != R.id.meuPerfilFragment);
        }

        setupBackPress();
    }

    private fun setupObservers() {
        sharedViewModel.usuarioLogado.observe(this) { usuario ->
            atualizarHeaderDrawer(usuario);
            configurarMenusPorPerfil(usuario);
        }
    }

    private fun configurarMenusPorPerfil(usuario: Usuario) {
        val perfil = usuario.perfil?.nome?.uppercase();

        bottomNavigationView.menu.clear();
        navigationView.menu.clear();

        if (perfil == "ADMINISTRADOR") {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_admin);
            navigationView.inflateMenu(R.menu.drawer_menu_admin);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_veterinario);
            navigationView.inflateMenu(R.menu.drawer_menu_veterinario);
        }

        bottomNavigationView.setupWithNavController(navController);
        navigationView.setupWithNavController(navController);
    }

    private fun atualizarHeaderDrawer(usuario: Usuario) {
        val headerView = navigationView.getHeaderView(0);
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_header_name);
        val userEmailTextView = headerView.findViewById<TextView>(R.id.nav_header_email);
        val headerImageView = headerView.findViewById<ImageView>(R.id.nav_header_image);

        userNameTextView.text = usuario.nome;
        userEmailTextView.text = usuario.email;

        Glide.with(this)
            .load(usuario.urlImagem)
            .placeholder(R.drawable.ic_profile_white)
            .circleCrop()
            .into(headerImageView);

        carregarImagemPerfilToolbar(usuario.urlImagem);
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_app_bar_menu, menu);
        val profileItem = menu?.findItem(R.id.profile_icon);
        val actionView = profileItem?.actionView;

        profileMenuItem = profileItem

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

    private fun showProfilePopupMenu(anchorView: View) {
        val popup = PopupMenu(this, anchorView);
        popup.menuInflater.inflate(R.menu.profile_popup_menu, popup.menu);

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popup_my_profile -> {
                    navController.navigate(R.id.action_global_meuPerfilFragment);
                    true;
                }
                R.id.popup_logout -> {
                    deslogarEVoltarParaLogin();
                    true;
                }
                else -> false;
            }
        }
        popup.show();
    }

    private fun carregarDadosDoUsuario() {

        sharedViewModel.setLoading(true);
        lifecycleScope.launch {
            val usuarioResponse = withContext(Dispatchers.IO) {
                usuarioController.getMeuPerfil();
            }

            if (usuarioResponse != null) {
                val usuarioLogado = Usuario(usuarioResponse);
                sharedViewModel.setUsuario(usuarioLogado);
            } else {
                Toast.makeText(this@HomeActivity, R.string.erro_carregar_usuario, Toast.LENGTH_LONG).show();
                deslogarEVoltarParaLogin();
            }
            sharedViewModel.setLoading(false);
        }

    }

    private fun deslogarEVoltarParaLogin() {
        sessionManager.clearAuthToken();
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }
        startActivity(intent);
        finish();
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
                        toolbarProfileProgressBar?.visibility = View.GONE;
                        return false;
                    }
                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        toolbarProfileProgressBar?.visibility = View.GONE;
                        return false;
                    }
                })
                .into(imageView);
        }
    }

    private fun setupBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    isEnabled = false;
                    onBackPressedDispatcher.onBackPressed();
                    isEnabled = true;
                }
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
    }

}