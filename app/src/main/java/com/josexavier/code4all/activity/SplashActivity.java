package com.josexavier.code4all.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;

public class SplashActivity extends AppCompatActivity {

    private FirebaseUser utilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definir o arquivo XML como layout
        setContentView(R.layout.activity_splash);

        utilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser();

        // "Atividade de Carregamento"
        carregamento();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Evocar o método para que a aplicação ocupe o ecrã inteiro
        esconderInterface();

    }

    void esconderInterface() {

        // Verificar se a versão do Android é maior ou igual a 9 (Android Pie)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        View visualizacao = getWindow().getDecorView();
        visualizacao.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private boolean temConexaoInternet() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo networkInfo : netInfo) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (networkInfo.isConnected())
                    haveConnectedWifi = true;
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (networkInfo.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void carregamento() {

        if (temConexaoInternet()) {

            Handler handler = new Handler();
            int delay = 0;
            if (utilizador == null) {
                delay = 2000;
            }

            final int tempoDelay = delay;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    handler.postDelayed(() -> introducao(), tempoDelay);
                }
            };

            thread.start();

        } else {
            Toast.makeText(this, "Precisa de uma ligação à internet!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void introducao() {
        if (utilizador != null) {
            Configs.buscarUtilizador(getApplicationContext(), intent -> {
                if (intent != null) {
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
    }

}