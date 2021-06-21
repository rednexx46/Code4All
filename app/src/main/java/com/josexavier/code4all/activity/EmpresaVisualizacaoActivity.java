package com.josexavier.code4all.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Empresa;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import dmax.dialog.SpotsDialog;

public class EmpresaVisualizacaoActivity extends AppCompatActivity {

    private LinearLayout linearLayoutEmpresa;
    private ImageView imageViewEmpresa;
    private TextView textViewNome, textViewDescricao, textViewDescricaoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_visualizacao);

        // Recuperar o id da empresa selecionada
        String idEmpresa = getIntent().getStringExtra("idEmpresa");

        // Configurações Iniciais
        linearLayoutEmpresa = findViewById(R.id.linearLayoutEmpresaVisualizacao);
        imageViewEmpresa = findViewById(R.id.imageViewEmpresaVisualizacao);
        textViewNome = findViewById(R.id.textViewNomeEmpresaVisualizacao);
        textViewDescricao = findViewById(R.id.textViewDescricaoEmpresaVisualizacao);
        textViewDescricaoData = findViewById(R.id.textViewDescricaoDataEmpresaVisualizacao);


        buscarEmpresa(idEmpresa);

    }

    private void buscarEmpresa(String idEmpresa) { // Buscar os dados da Empresa Selecionada, com o idEmpresa
        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Empresa...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        DatabaseReference empresaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idEmpresa);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialog.show();
                Empresa empresa = snapshot.getValue(Empresa.class);

                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.borda_preta);
                drawable.setColorFilter(empresa.getCorFundoPerfil(), PorterDuff.Mode.SRC);

                linearLayoutEmpresa.setBackground(drawable);
                Glide.with(getApplicationContext()).load(empresa.getFoto()).into(imageViewEmpresa);
                textViewNome.setText(empresa.getNome());
                textViewDescricao.setText(empresa.getDescricao());
                textViewDescricaoData.setText("Última vez atualizado .: " + empresa.getDataDescricao());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}