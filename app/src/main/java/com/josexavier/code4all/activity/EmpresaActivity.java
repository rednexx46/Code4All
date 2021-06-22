package com.josexavier.code4all.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmpresaActivity extends AppCompatActivity {

    public static final String PREFS = "numero_execucoes";
    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth autenticacao;

    private CircleImageView perfil;
    private TextView nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem logout = navigationView.getMenu().findItem(R.id.nav_logout);

        logout.setOnMenuItemClickListener(item -> {
            autenticacao.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
            startActivity(intent);
            return true;
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio_empresa,
                R.id.nav_ofertas_verificacao,
                R.id.nav_empresas,
                R.id.nav_criar_oferta, R.id.nav_listar_ofertas,
                R.id.nav_empresa,
                R.id.nav_definicoes)
                .setDrawerLayout(drawer)
                .build();


        autenticacao = DefinicaoFirebase.recuperarAutenticacao();

        View header = navigationView.getHeaderView(0);

        perfil = header.findViewById(R.id.menufotoPerfil);
        nome = header.findViewById(R.id.menuNomePerfil);

        ImageButton menu = findViewById(R.id.botaoMenu);

        menu.setOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.START);
        });

        RelativeLayout footer = (RelativeLayout) navigationView.getMenu().findItem(R.id.nav_footer).getActionView();
        try {
            TextView versao = footer.findViewById(R.id.textPrincipalVersao);
            versao.setOnClickListener(v -> Configs.quantidadeVezesExecucao(getApplicationContext()));
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String version = pInfo.versionName;
            versao.setText("Code4All " + version);
        } catch (Exception e) {
            Toast.makeText(this, "ERRO : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        vezesExecucao();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Glide.with(getApplicationContext()).load(autenticacao.getCurrentUser().getPhotoUrl()).into(perfil);
        nome.setText(autenticacao.getCurrentUser().getDisplayName());

    }

    private void vezesExecucao() {
        SharedPreferences settings = this.getSharedPreferences(PREFS, 0);
        int vezes = settings.getInt("vezes", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("vezes", vezes + 1);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void abrirNotificacoes(View view) {
        Intent notificacoes = new Intent(getApplicationContext(), NotificacoesActivity.class);
        startActivity(notificacoes);
    }
}