package br.com.caiorodri.agenpet.ui.usuario;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewModelScope;
import br.com.caiorodri.agenpet.api.controller.UsuarioController;
import br.com.caiorodri.agenpet.model.usuario.VeterinarioHorario;
import kotlinx.coroutines.launch;

class HorarioViewModel(application: Application) : AndroidViewModel(application) {

    private val controller = UsuarioController(application);

    private val _listaHorarios = MutableLiveData<List<VeterinarioHorario>>();
    val listaHorarios: LiveData<List<VeterinarioHorario>> = _listaHorarios;

    private val _isLoading = MutableLiveData<Boolean>(false);
    val isLoading: LiveData<Boolean> = _isLoading;

    private val _erro = MutableLiveData<String?>();
    val erro: LiveData<String?> = _erro;

    fun carregarHorarios(idVeterinario: Long) {

        viewModelScope.launch {

            _isLoading.value = true;

            try {

                val lista = controller.listarHorariosVeterinario(idVeterinario);
                _listaHorarios.postValue(lista);

            } catch (e: Exception) {

                _erro.postValue("Erro ao carregar: ${e.message}");

            } finally {

                _isLoading.value = false;

            }
        }
    }

    fun salvarHorario(horario: VeterinarioHorario) {

        viewModelScope.launch {

            _isLoading.value = true;

            try {

                val salvo = controller.salvarHorario(horario);

                if (salvo != null) {

                    carregarHorarios(horario.idVeterinario!!);

                } else {

                    _erro.postValue("Erro ao salvar horário.");

                }

            } catch (e: Exception) {

                _erro.postValue(e.message);

            } finally {

                _isLoading.value = false;

            }
        }
    }

    fun deletarHorario(idHorario: Long, idVeterinario: Long) {

        viewModelScope.launch {

            _isLoading.value = true;

            try {

                val sucesso = controller.deletarHorario(idHorario);

                if (sucesso) {

                    carregarHorarios(idVeterinario);

                } else {

                    _erro.postValue("Erro ao deletar.");

                }

            } catch (e: Exception) {

                _erro.postValue(e.message);

            } finally {

                _isLoading.value = false;

            }
        }
    }
}