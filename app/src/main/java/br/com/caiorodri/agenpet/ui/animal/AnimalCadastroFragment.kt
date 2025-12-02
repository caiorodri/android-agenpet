package br.com.caiorodri.agenpet.ui.animal;

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.caiorodri.agenpet.R
import br.com.caiorodri.agenpet.databinding.FragmentAnimalCadastroBinding
import br.com.caiorodri.agenpet.model.animal.Animal
import br.com.caiorodri.agenpet.model.enums.PerfilEnum
import br.com.caiorodri.agenpet.model.usuario.Usuario
import br.com.caiorodri.agenpet.model.usuario.UsuarioResponse
import br.com.caiorodri.agenpet.ui.home.ClienteHomeActivity
import br.com.caiorodri.agenpet.ui.home.ClienteHomeSharedViewModel
import br.com.caiorodri.agenpet.ui.home.HomeActivity
import br.com.caiorodri.agenpet.ui.home.HomeSharedViewModel
import br.com.caiorodri.agenpet.utils.getNomeTraduzido
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class AnimalCadastroFragment : Fragment() {

    private var _binding: FragmentAnimalCadastroBinding? = null;
    private val binding get() = _binding!!;
    private val viewModel: AnimalCadastroViewModel by viewModels();
    private var usuarioLogado: Usuario? = null;
    private val args: AnimalCadastroFragmentArgs by navArgs();
    private var animalParaEdicao: Animal? = null;
    private var isRecepcionista = false;
    private var listaClientes: List<UsuarioResponse> = emptyList();
    private val formatadorDeData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC");
    };

    private val uiHandler = Handler(Looper.getMainLooper());

    private val storage = Firebase.storage;
    private var fotoUrlExistente: String? = null;

    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            binding.progressBarFoto.visibility = View.GONE;
            binding.imageViewFotoPet.isEnabled = true;
            return@registerForActivityResult;
        }

        try {
            val pfd = requireContext().contentResolver.openFileDescriptor(uri, "r");
            val tamanhoEmBytes = pfd?.statSize;
            pfd?.close();
            val maxTamanhoEmMB = 30;
            val maxTamanhoEmBytes = maxTamanhoEmMB * 1024 * 1024;
            if (tamanhoEmBytes != null && tamanhoEmBytes > maxTamanhoEmBytes) {
                Toast.makeText(context, getString(R.string.erro_imagem_muito_grande_animal, maxTamanhoEmMB), Toast.LENGTH_LONG).show();
                binding.progressBarFoto.visibility = View.GONE;
                binding.imageViewFotoPet.isEnabled = true;
                return@registerForActivityResult;
            }

            viewModel.fotoUriSelecionada.value = uri;

        } catch (e: Exception) {
            Log.e("AnimalCadastroFragment", "Erro ao ler o tamanho do arquivo", e);
            Toast.makeText(context, getString(R.string.erro_selecionar_imagem_animal), Toast.LENGTH_SHORT).show();
            binding.progressBarFoto.visibility = View.GONE;
            binding.imageViewFotoPet.isEnabled = true;
        }
    };

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentAnimalCadastroBinding.inflate(inflater, container, false);

        return binding.root;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState);

        animalParaEdicao = args.animal;

        val activity = requireActivity();

        if (activity is ClienteHomeActivity) {

            val sharedVM = androidx.lifecycle.ViewModelProvider(activity)[ClienteHomeSharedViewModel::class.java];
            usuarioLogado = sharedVM.usuarioLogado.value;

        } else if (activity is HomeActivity) {

            val sharedVM = androidx.lifecycle.ViewModelProvider(activity)[HomeSharedViewModel::class.java];
            usuarioLogado = sharedVM.usuarioLogado.value;

        }

        isRecepcionista = usuarioLogado?.perfil?.id == PerfilEnum.RECEPCIONISTA.id;

        viewModel.carregarDadosIniciais(isRecepcionista);

        setupUIBase();
        setupListeners();
        setupObservers();
    }

    private fun setupUIBase() {

        binding.autoCompleteEspecie.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyList<String>())
        );

        binding.autoCompleteRaca.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyList<String>())
        );

        binding.autoCompleteCliente.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyList<String>())
        );

        if (isRecepcionista) {
            binding.menuCliente.visibility = View.VISIBLE;
        } else {
            binding.menuCliente.visibility = View.GONE;
        }

        val animal = animalParaEdicao;

        binding.buttonSalvar.isEnabled = false;

        if (animal != null) {

            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.titulo_editar_animal);

            binding.textViewIdAnimal.text = getString(R.string.placeholder_id_animal, animal.id ?: 0);
            binding.textViewIdAnimal.isVisible = true;
            binding.buttonRemover.isVisible = true;

            binding.editTextNome.setText(animal.nome);
            binding.editTextDescricao.setText(animal.descricao);

            animal.dataNascimento?.let { timestamp ->
                binding.editTextDataNascimento.setText(formatadorDeData.format(Date(timestamp)));
            };

            val sexoId = animal.sexo?.id;
            when (sexoId) {
                1 -> binding.toggleButtonGroupSexo.check(R.id.button_macho);
                2 -> binding.toggleButtonGroupSexo.check(R.id.button_femea);
                3 -> binding.toggleButtonGroupSexo.check(R.id.button_desconhecido);
                else -> binding.toggleButtonGroupSexo.clearChecked();
            }

            binding.editTextPeso.setText(animal.peso?.toString());
            binding.editTextAltura.setText(animal.altura?.toString());

            if (animal.castrado == true) {
                binding.radioCastradoSim.isChecked = true;
            } else {
                binding.radioCastradoNao.isChecked = true;
            }

            binding.inputLayoutPeso.isEnabled = false;
            binding.inputLayoutAltura.isEnabled = false;
            binding.radioGroupCastrado.children.forEach { it.isEnabled = false };
            binding.labelInfoAdicional.isVisible = true;

            binding.buttonSalvar.text = getString(R.string.button_atualizar);

            fotoUrlExistente = animal.urlImagem;

            if (viewModel.fotoUriSelecionada.value == null) {
                binding.progressBarFoto.visibility = View.VISIBLE;
                binding.imageViewFotoPet.isEnabled = false;

                Glide.with(this)
                    .load(fotoUrlExistente)
                    .placeholder(R.drawable.ic_pet)
                    .error(R.drawable.ic_pet)
                    .circleCrop()
                    .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                        override fun onLoadFailed(e: com.bumptech.glide.load.engine.GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                            binding.progressBarFoto.visibility = View.GONE;
                            binding.imageViewFotoPet.isEnabled = true;
                            return false;
                        }
                        override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>, dataSource: com.bumptech.glide.load.DataSource, isFirstResource: Boolean): Boolean {
                            binding.progressBarFoto.visibility = View.GONE;
                            binding.imageViewFotoPet.isEnabled = true;
                            return false;
                        }
                    })
                    .into(binding.imageViewFotoPet);
            }

        } else {

            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.label_cadastro_pet);

            binding.textViewIdAnimal.isVisible = false;
            binding.buttonRemover.isVisible = false;
            binding.labelInfoAdicional.isVisible = true;
            binding.buttonSalvar.text = getString(R.string.button_salvar);
            binding.inputLayoutPeso.isEnabled = true;
            binding.inputLayoutAltura.isEnabled = true;
            binding.radioGroupCastrado.children.forEach { it.isEnabled = true };
            binding.menuRaca.isEnabled = false;

            if(isRecepcionista) binding.menuCliente.isEnabled = true;

        }
    }

    private fun setupListeners() {

        binding.inputLayoutDataNascimento.setEndIconOnClickListener { mostrarDatePicker(); };
        binding.editTextDataNascimento.setOnClickListener { mostrarDatePicker(); };

        binding.autoCompleteEspecie.setOnItemClickListener { parent, _, position, _ ->
            val nomeEspecie = parent.getItemAtPosition(position) as String;
            val especie = viewModel.especies.value?.find { it.getNomeTraduzido(requireContext()) == nomeEspecie };

            binding.autoCompleteRaca.setText(null, false);
            viewModel.setEspecie(especie);
        };

        binding.autoCompleteRaca.setOnItemClickListener { parent, _, position, _ ->
            val nomeRaca = parent.getItemAtPosition(position) as String;
            val especieAtual = viewModel.especieSelecionada.value;
            val raca = viewModel.listaCompletaRacas.find {
                it.getNomeTraduzido(requireContext()) == nomeRaca && it.especie?.id == especieAtual?.id
            };
            viewModel.setRaca(raca);
        };

        if(isRecepcionista) {

            binding.autoCompleteCliente.setOnItemClickListener { parent, _, position, _ ->

                val nomeCliente = parent.getItemAtPosition(position) as String;
                val cliente = listaClientes.find { it.nome == nomeCliente }

                viewModel.setClienteSelecionado(cliente);

            }

        }

        binding.buttonSalvar.setOnClickListener {
            salvarDadosDoAnimal();
        };

        binding.buttonRemover.setOnClickListener {
            mostrarDialogoDelecao();
        }

        binding.imageViewFotoPet.setOnClickListener {
            binding.progressBarFoto.visibility = View.VISIBLE;
            binding.imageViewFotoPet.isEnabled = false;
            selecionarImagemLauncher.launch("image/*");
        }

    }

    private fun setupObservers() {

        viewModel.clientes.observe(viewLifecycleOwner) { clientes ->

            listaClientes = clientes;

            val nomes = clientes.map { it.nome }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomes);

            binding.autoCompleteCliente.setAdapter(adapter);

        }

        viewModel.especies.observe(viewLifecycleOwner) { especies ->

            val nomesDasEspecies = especies.map { it.getNomeTraduzido(requireContext()) };

            val novoAdapterEspecie = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesDasEspecies);
            binding.autoCompleteEspecie.setAdapter(novoAdapterEspecie);

            if (animalParaEdicao != null && viewModel.especieSelecionada.value == null) {
                val especieDoAnimal = animalParaEdicao?.raca?.especie;
                if (especieDoAnimal != null) {
                    viewModel.setEspecie(especieDoAnimal);
                }
            }

        }

        viewModel.especieSelecionada.observe(viewLifecycleOwner) { especie ->
            if (especie != null) {
                binding.autoCompleteEspecie.setText(especie.getNomeTraduzido(requireContext()), false);
            } else {
                if (animalParaEdicao == null) {
                    binding.autoCompleteEspecie.setText(null, false);
                }
            }
        }

        viewModel.racasFiltradas.observe(viewLifecycleOwner) { racas ->

            val nomesDasRacas = racas.map { it.getNomeTraduzido(requireContext()) };
            val novoAdapterRaca = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nomesDasRacas);
            binding.autoCompleteRaca.setAdapter(novoAdapterRaca);

            binding.menuRaca.isEnabled = racas.isNotEmpty();

            if (animalParaEdicao != null && viewModel.racaSelecionada.value == null) {
                val racaDoAnimal = animalParaEdicao?.raca;
                if (racaDoAnimal != null && racas.any { it.id == racaDoAnimal.id }) {
                    viewModel.setRaca(racaDoAnimal);
                }
            }
        }

        viewModel.racaSelecionada.observe(viewLifecycleOwner) { raca ->
            if (raca != null) {
                binding.autoCompleteRaca.setText(raca.getNomeTraduzido(requireContext()), false);
            } else {
                binding.autoCompleteRaca.setText(null, false);
            }
        }

        viewModel.isLoadingDadosIniciais.observe(viewLifecycleOwner) { isLoading ->

            binding.progressBarRacas?.isVisible = isLoading;
            binding.menuEspecie.isEnabled = !isLoading;
            binding.buttonSalvar.isEnabled = !isLoading;

            if (isRecepcionista) binding.menuCliente.isEnabled = !isLoading;

            if(isLoading) {

                binding.menuRaca.isEnabled = false;

            } else {

                val temRacas = viewModel.racasFiltradas.value?.isNotEmpty() ?: false
                binding.menuRaca.isEnabled = temRacas

            }

        }

        viewModel.erroDadosIniciais.observe(viewLifecycleOwner) { erro ->
            erro?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show(); };
        };

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.savingOverlay.isVisible = isLoading;
            binding.buttonSalvar.isEnabled = !isLoading;
            binding.buttonRemover.isEnabled = !isLoading;
        }

        viewModel.actionError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                viewModel.resetActionError();
            }
        }

        viewModel.animalSalvoComSucesso.observe(viewLifecycleOwner) { animalResponseSalvo ->

            animalResponseSalvo ?: return@observe;

            Toast.makeText(context, getString(R.string.toast_animal_salvo_sucesso), Toast.LENGTH_SHORT).show();

            val activity = requireActivity();

            if (activity is ClienteHomeActivity) {

                val animalObj = Animal(animalResponseSalvo).copy(agendamentos = animalParaEdicao?.agendamentos);
                val sharedVM = ViewModelProvider(activity)[ClienteHomeSharedViewModel::class.java];

                sharedVM.atualizarAnimalLocalmente(animalObj);
                activity.carregarDadosDoUsuario();

            } else if (activity is HomeActivity) {

                activity.carregarDadosDoUsuario();

            }

            viewModel.resetAnimalSalvo();
            findNavController().popBackStack();
        }

        viewModel.animalRemovidoComSucesso.observe(viewLifecycleOwner) { removido ->

            removido ?: return@observe;

            Toast.makeText(context, getString(R.string.toast_animal_removido_sucesso), Toast.LENGTH_SHORT).show();

            val activity = requireActivity();

            if (activity is ClienteHomeActivity) {

                val sharedVM = ViewModelProvider(activity)[ClienteHomeSharedViewModel::class.java];

                animalParaEdicao?.id?.let { sharedVM.removerAnimalLocalmente(it) }
                activity.carregarDadosDoUsuario();

            } else if (activity is HomeActivity) {

                activity.carregarDadosDoUsuario();

            }

            viewModel.resetAnimalRemovido();
            findNavController().popBackStack();
        }

        viewModel.fotoUriSelecionada.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {

                binding.progressBarFoto.visibility = View.VISIBLE;
                binding.imageViewFotoPet.isEnabled = false;

                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                        override fun onLoadFailed(e: com.bumptech.glide.load.engine.GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                            binding.progressBarFoto.visibility = View.GONE;
                            binding.imageViewFotoPet.isEnabled = true;
                            return false;
                        }
                        override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>, dataSource: com.bumptech.glide.load.DataSource, isFirstResource: Boolean): Boolean {
                            binding.progressBarFoto.visibility = View.GONE;
                            binding.imageViewFotoPet.isEnabled = true;
                            return false;
                        }
                    })
                    .into(binding.imageViewFotoPet);
            }
        }
    }

    private fun mostrarDialogoDelecao() {
        val animalId = animalParaEdicao?.id;
        val animalNome = animalParaEdicao?.nome;
        if (animalId == null || animalNome == null) return;

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_remover_animal))
            .setMessage(getString(R.string.dialog_message_remover_animal, animalNome))
            .setNegativeButton(getString(R.string.dialog_button_cancelar)) { dialog, _ ->
                dialog.dismiss();
            }
            .setPositiveButton(getString(R.string.dialog_button_remover)) { dialog, _ ->
                viewModel.removerAnimal(animalId);
                dialog.dismiss();
            }
            .show();
    }

    private fun salvarDadosDoAnimal() {

        if (!validarCampos()) {
            Toast.makeText(context, getString(R.string.toast_preencher_campos_obrigatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            viewModel.setIsLoading(true);

            val animal = criarObjetoAnimal(usuarioLogado, fotoUrlExistente);

            val uriSelecionado = viewModel.fotoUriSelecionada.value;

            if (uriSelecionado != null) {

                comprimirEUploadImagem(uriSelecionado) { downloadUrl ->

                    val animalComFoto = criarObjetoAnimal(usuarioLogado, downloadUrl);
                    viewModel.salvarAnimal(animalComFoto);

                }

            } else {

                viewModel.salvarAnimal(animal);

            }


        } catch (e: Exception) {

            viewModel.setIsLoading(false);
            Log.e("AnimalCadastroFragment", getString(R.string.erro_criar_objeto_animal), e);
            Toast.makeText(context, e.message ?: getString(R.string.erro_validar_dados_animal), Toast.LENGTH_SHORT).show();

        }

    }

    private fun comprimirEUploadImagem(uri: Uri, onSuccess: (String) -> Unit) {

        val MAX_DIMENSION = 1080;

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .override(MAX_DIMENSION, MAX_DIMENSION)
            .centerInside()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    val baos = ByteArrayOutputStream();

                    resource.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    val dataEmBytes = baos.toByteArray();

                    Log.d("AnimalCadastroFragment", "Imagem comprimida para ${dataEmBytes.size / 1024} KB");

                    uploadBytesParaFirebase(dataEmBytes, onSuccess);

                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {

                    Log.e("AnimalCadastroFragment", "Falha ao carregar/comprimir imagem com Glide");

                    Toast.makeText(context, getString(R.string.erro_processar_imagem_animal), Toast.LENGTH_SHORT).show();

                    viewModel.setIsLoading(false);

                    binding.progressBarFoto.visibility = View.GONE;
                    binding.imageViewFotoPet.isEnabled = true;
                }
            });
    }

    private fun uploadBytesParaFirebase(data: ByteArray, onSuccess: (String) -> Unit) {
        val nomeArquivo = "${UUID.randomUUID()}.jpg";
        val ref = storage.reference.child("imagens/animais/$nomeArquivo");

        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString());
                }.addOnFailureListener { e ->
                    Log.e("AnimalCadastroFragment", "Erro ao obter URL de download", e);
                    Toast.makeText(context, getString(R.string.erro_obter_url_animal, e.message), Toast.LENGTH_SHORT).show();
                    viewModel.setIsLoading(false);
                }
            }
            .addOnFailureListener { e ->
                Log.e("AnimalCadastroFragment", "Erro no upload da imagem", e);
                Toast.makeText(context, getString(R.string.erro_upload_imagem_animal, e.message), Toast.LENGTH_SHORT).show();
                viewModel.setIsLoading(false);
            };
    }

    private fun criarObjetoAnimal(donoLogado: Usuario?, urlDaFoto: String?): Animal {

        val donoFinal: Usuario;

        if (isRecepcionista) {

            if (animalParaEdicao != null) {

                donoFinal = animalParaEdicao!!.dono!!

            } else {

                val clienteId = viewModel.clienteSelecionadoId.value ?: throw Exception("Selecione um cliente.")
                val clienteResponse = listaClientes.find { it.id == clienteId } ?: throw Exception("Cliente inválido.")

                donoFinal = Usuario(
                    id = clienteResponse.id,
                    nome = clienteResponse.nome,
                    email = clienteResponse.email,
                    ""
                )

            }

        } else {

            donoFinal = donoLogado ?: throw Exception("Usuário não logado.")

        }

        val dataNascimentoString = binding.editTextDataNascimento.text.toString();

        val dataNascimentoTimestamp = if (dataNascimentoString.isNotBlank()) {

            try {

                formatadorDeData.parse(dataNascimentoString)?.time;

            } catch (e: Exception) { null }

        } else { null };

        val nome = binding.editTextNome.text.toString().trim();
        val descricao = binding.editTextDescricao.text.toString().trim();

        val nomeEspecieTraduzido = binding.autoCompleteEspecie.text.toString();
        val especie = viewModel.especies.value?.find { it.getNomeTraduzido(requireContext()) == nomeEspecieTraduzido }

        val nomeRacaTraduzido = binding.autoCompleteRaca.text.toString();
        val raca = viewModel.listaCompletaRacas.find { it.getNomeTraduzido(requireContext()) == nomeRacaTraduzido && (it.especie?.equals(especie) == true) }
            ?: run {
                if (binding.menuRaca.isEnabled) {
                    binding.menuRaca.error = getString(R.string.erro_raca_obrigatoria);
                    throw Exception(getString(R.string.erro_raca_obrigatoria));
                }
                null;
            };

        val sexoId = when (binding.toggleButtonGroupSexo.checkedButtonId) {
            R.id.button_macho -> 1;
            R.id.button_femea -> 2;
            R.id.button_desconhecido -> 3;
            else -> throw Exception(getString(R.string.toast_selecionar_sexo));
        };

        val sexo = viewModel.sexos.value?.find { it.id == sexoId }
            ?: throw Exception("Objeto Sexo (ID: $sexoId) não encontrado no ViewModel.");

        val peso = if (animalParaEdicao != null) animalParaEdicao?.peso else binding.editTextPeso.text.toString().toDoubleOrNull();
        val altura = if (animalParaEdicao != null) animalParaEdicao?.altura else binding.editTextAltura.text.toString().toDoubleOrNull();
        val castrado = if (animalParaEdicao != null) animalParaEdicao?.castrado else binding.radioGroupCastrado.checkedRadioButtonId == R.id.radio_castrado_sim;

        return Animal(
            id = animalParaEdicao?.id,
            nome = nome,
            dono = donoFinal,
            dataNascimento = dataNascimentoTimestamp,
            descricao = descricao,
            raca = raca,
            sexo = sexo,
            peso = peso,
            altura = altura,
            castrado = castrado,
            agendamentos = animalParaEdicao?.agendamentos,
            urlImagem = urlDaFoto
        );
    }

    private fun validarCampos(): Boolean {

        var camposValidos = true;

        binding.inputLayoutNome.error = null;
        binding.menuEspecie.error = null;
        binding.menuRaca.error = null;
        binding.inputLayoutPeso.error = null;
        binding.inputLayoutAltura.error = null;
        binding.menuCliente.error = null;

        if (binding.editTextNome.text.isNullOrBlank()) {
            binding.inputLayoutNome.error = getString(R.string.erro_nome_obrigatorio);
            camposValidos = false;
        }

        if (binding.autoCompleteEspecie.text.isNullOrBlank()) {
            binding.menuEspecie.error = getString(R.string.erro_especie_obrigatoria);
            camposValidos = false;
        }

        if (binding.menuRaca.isEnabled && binding.autoCompleteRaca.text.isNullOrBlank()) {
            binding.menuRaca.error = getString(R.string.erro_raca_obrigatoria);
            camposValidos = false;
        }

        if (isRecepcionista && animalParaEdicao == null && binding.autoCompleteCliente.text.isNullOrBlank()) {
            binding.menuCliente.error = getString(R.string.erro_obrigatorio)
            camposValidos = false
        }

        if (binding.toggleButtonGroupSexo.checkedButtonId == View.NO_ID) {
            Toast.makeText(context, getString(R.string.toast_selecionar_sexo), Toast.LENGTH_SHORT).show();
            camposValidos = false;
        }

        if (animalParaEdicao == null) {
            if (binding.editTextPeso.text.isNullOrBlank()) {
                binding.inputLayoutPeso.error = getString(R.string.erro_obrigatorio);
                camposValidos = false;
            }
            if (binding.editTextAltura.text.isNullOrBlank()) {
                binding.inputLayoutAltura.error = getString(R.string.erro_obrigatorio);
                camposValidos = false;
            }
            if (binding.radioGroupCastrado.checkedRadioButtonId == -1) {
                Toast.makeText(context, getString(R.string.toast_informar_castrado), Toast.LENGTH_SHORT).show();
                camposValidos = false;
            }
        }

        return camposValidos;
    }

    private fun mostrarDatePicker() {

        val constraintsBuilder = CalendarConstraints.Builder();
        val hojeEmUtc = MaterialDatePicker.todayInUtcMilliseconds();
        val validator = DateValidatorPointBackward.now();

        constraintsBuilder.setEnd(hojeEmUtc);
        constraintsBuilder.setValidator(validator);

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.titulo_datepicker_data_nascimento))
            .setCalendarConstraints(constraintsBuilder.build())
            .build();

        datePicker.addOnPositiveButtonClickListener { dataTimestamp ->
            val data = Date(dataTimestamp);
            binding.editTextDataNascimento.setText(formatadorDeData.format(data));
        };

        datePicker.show(childFragmentManager, "DATE_PICKER");
    }


    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
        uiHandler.removeCallbacksAndMessages(null);
    }
}