package com.josexavier.code4all.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.Permissao;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    private final String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        // Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        // Desativar o indicador das páginas
        setPagerIndicatorVisible(false);

        // Recuperar o número de vezes que a app foi executada
        String PREFS = "numero_execucoes";
        SharedPreferences settings = this.getSharedPreferences(PREFS, 0);
        int vezes = settings.getInt("vezes", 0);

        if (vezes > 0) { // se for maior que zero, apenas apresenta o layout de Autenticação

            addSlide(new SimpleSlide.Builder()
                    .layout(R.layout.intro_entrar)
                    .background(android.R.color.white)
                    .canGoBackward(false)
                    .canGoForward(false)
                    .build());

        } else { // se não, mostra todos os layouts
            addSlide(new SimpleSlide.Builder()
                    .layout(R.layout.intro_um)
                    .background(R.color.corIntroUm)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .layout(R.layout.intro_dois)
                    .background(R.color.corIntroDois)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .layout(R.layout.intro_tres)
                    .background(R.color.corIntroTres)
                    .build());

            addSlide(new SimpleSlide.Builder()
                    .layout(R.layout.intro_entrar)
                    .background(android.R.color.white)
                    .canGoBackward(false)
                    .canGoForward(false)
                    .build());
        }

        // Desativar os botoes de navegacao chatos
        setButtonBackVisible(false);
        setButtonNextVisible(false);


    }

    public void entrarContaEmail(View view) {
        Intent atividadeLogin = new Intent(this, LoginActivity.class);
        atividadeLogin.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(atividadeLogin);
    }

    public void criarConta(View view) {
        Intent atividadeCriarConta = new Intent(this, CriarContaActivity.class);
        atividadeCriarConta.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(atividadeCriarConta);
    }

}