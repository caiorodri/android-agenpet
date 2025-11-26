package br.com.caiorodri.agenpet.utils

import android.content.Context;
import br.com.caiorodri.agenpet.R;
import br.com.caiorodri.agenpet.model.agendamento.Status;
import br.com.caiorodri.agenpet.model.agendamento.Tipo;
import br.com.caiorodri.agenpet.model.animal.Especie
import br.com.caiorodri.agenpet.model.animal.Raca
import br.com.caiorodri.agenpet.model.animal.Sexo;


fun Status.getNomeTraduzido(context: Context): String {

    val stringId = when (this.id) {
        1 -> R.string.status_aberto;
        2 -> R.string.status_cancelado;
        3 -> R.string.status_concluido;
        4 -> R.string.status_perdido;
        else -> R.string.status_desconhecido;
    }

    return context.getString(stringId);

}

fun Tipo.getNomeTraduzido(context: Context): String {

    val stringId = when (this.nome.lowercase()) {
        "consulta" -> R.string.tipo_consulta;
        "cirurgia" -> R.string.tipo_cirurgia;
        "vacina" -> R.string.tipo_vacina;
        else -> R.string.tipo_outro;
    }

    return context.getString(stringId);

}

fun Especie.getNomeTraduzido(context: Context): String {

    val stringId = when (this.nome) {
        "Cachorro" -> R.string.especie_cachorro;
        "Gato" -> R.string.especie_gato;
        "Ave" -> R.string.especie_ave;
        "Coelho" -> R.string.especie_coelho;
        "Tartaruga" -> R.string.especie_tartaruga;
        "Peixe" -> R.string.especie_peixe;
        "Cavalo" -> R.string.especie_cavalo;
        "Porquinho-da-índia" -> R.string.especie_porquinho_da_india;
        "Réptil" -> R.string.especie_reptil;
        "Rato" -> R.string.especie_rato;
        "Furão" -> R.string.especie_furao;
        "Hamster" -> R.string.especie_hamster;
        else -> R.string.especie_outros;
    }

    return context.getString(stringId);

}

fun Raca.getNomeTraduzido(context: Context): String {

    val stringId = when (this.nome) {
        "Desconhecido" -> R.string.raca_desconhecido;
        "Vira Lata" -> R.string.raca_vira_lata;

        "Pastor Alemão" -> R.string.raca_pastor_alemao;
        "Poodle" -> R.string.raca_poodle;
        "Labrador Retriever" -> R.string.raca_labrador_retriever;
        "Bulldog Francês" -> R.string.raca_bulldog_frances;
        "Golden Retriever" -> R.string.raca_golden_retriever;
        "Chihuahua" -> R.string.raca_chihuahua;
        "Beagle" -> R.string.raca_beagle;
        "Shih Tzu" -> R.string.raca_shih_tzu;
        "Dachshund" -> R.string.raca_dachshund;
        "Boxer" -> R.string.raca_boxer;
        "Rottweiler" -> R.string.raca_rottweiler;

        "Siamês" -> R.string.raca_siames;
        "Persa" -> R.string.raca_persa;
        "Maine Coon" -> R.string.raca_maine_coon;
        "Angorá" -> R.string.raca_angora;
        "Sphynx" -> R.string.raca_sphynx;
        "British Shorthair" -> R.string.raca_british_shorthair;
        "Bengal" -> R.string.raca_bengal;
        "Ragdoll" -> R.string.raca_ragdoll;

        "Periquito" -> R.string.raca_periquito;
        "Calopsita" -> R.string.raca_calopsita;
        "Papagaio" -> R.string.raca_papagaio;
        "Canário" -> R.string.raca_canario;
        "Arara" -> R.string.raca_arara;

        "Mini Lop" -> R.string.raca_mini_lop;
        "Lionhead" -> R.string.raca_lionhead;
        "Rex" -> R.string.raca_rex;
        "Angorá Inglês" -> R.string.raca_angora_ingles;

        "Betta" -> R.string.raca_betta;
        "Goldfish" -> R.string.raca_goldfish;
        "Tetra Neon" -> R.string.raca_tetra_neon;

        "Mangalarga Marchador" -> R.string.raca_mangalarga_marchador;
        "Puro Sangue Árabe" -> R.string.raca_puro_sangue_arabe;

        "Hamster Sírio" -> R.string.raca_hamster_sirio;
        "Hamster Anão Russo" -> R.string.raca_hamster_anao_russo;

        else -> R.string.raca_desconhecido;
    }
    return context.getString(stringId);
}

fun Sexo.getNomeTraduzido(context: Context): String {

    val stringId = when (this.id) {
        1 -> R.string.option_macho;
        2 -> R.string.option_femea;
        3 -> R.string.option_desconhecido;
        else -> R.string.option_desconhecido;
    }

    return context.getString(stringId);
}