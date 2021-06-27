package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Empresa;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class EmpresaConfiguracaoActivity extends AppCompatActivity {

    private final int SELECAO_CAMARA = 100;
    private final int SELECAO_GALERIA = 200;
    private ImageView imageViewEmpresa;
    private List<String> listaEmpregados = new ArrayList<>();
    private Bitmap imagem = null;
    private byte[] dadosImagem;
    private Empresa empresa;
    private AlertDialog dialog, dialogGuardando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_configuracao);

        empresa = (Empresa) getIntent().getSerializableExtra("empresa");

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogGuardando = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Configurações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        imageViewEmpresa = findViewById(R.id.imageViewEmpresaConfiguracao);

        TextView textViewSubTituloEmpresa;
        textViewSubTituloEmpresa = findViewById(R.id.textViewSubTituloEmpresaConfiguracao);
        textViewSubTituloEmpresa.setText(empresa.getNome());

        Spinner spinnerEmpregados;
        spinnerEmpregados = findViewById(R.id.spinnerEmpregadosEmpresaConfiguracao);
        buscarEmpregados(spinnerEmpregados);

        EditText editTextLocalidade, editTextDescricao;
        editTextLocalidade = findViewById(R.id.editTextLocalidadeEmpresaConfiguracao);
        editTextDescricao = findViewById(R.id.editTextDescricaoEmpresaConfiguracao);

        ImageButton imageButtonGaleria;
        imageButtonGaleria = findViewById(R.id.imageButtonGaleriaEmpresaConfiguracao);
        imageButtonGaleria.setOnClickListener(v -> abrirGaleria());


        Button buttonGuardar;
        buttonGuardar = findViewById(R.id.buttonSubmeterFotoEmpresaConfiguracao);
        buttonGuardar.setOnClickListener(v -> {
            String localidade = editTextLocalidade.getText().toString();
            String descricao = editTextDescricao.getText().toString();

            if (imagem != null) {
                if (spinnerEmpregados.getSelectedItemPosition() != 0) {
                    if (!localidade.isEmpty()) {
                        if (!descricao.isEmpty()) {
                            dialogGuardando.show();
                            empresa.setId(Configs.recuperarIdUtilizador());
                            empresa.setDataInscricao(Configs.recuperarDataHoje());
                            empresa.setDescricao(descricao);
                            empresa.setLocalidade(localidade);
                            empresa.setEmpregados(listaEmpregados.get(spinnerEmpregados.getSelectedItemPosition()));
                            empresa.guardarImagem(dadosImagem, sucesso -> { // 1º guarda img
                                if (sucesso) {
                                    empresa.guardar(sucesso2 -> { // 2º guarda dados
                                        if (sucesso2) {
                                            Toast.makeText(getApplicationContext(), "Empresa Configurada com Sucesso!", Toast.LENGTH_SHORT).show();
                                            Intent intentEmpresaActivity = new Intent(getApplicationContext(), EmpresaActivity.class);
                                            intentEmpresaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            dialogGuardando.dismiss();
                                            startActivity(intentEmpresaActivity);
                                            finish();
                                        } else {
                                            dialogGuardando.dismiss();
                                            Toast.makeText(EmpresaConfiguracaoActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    dialogGuardando.dismiss();
                                    Toast.makeText(EmpresaConfiguracaoActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Introduza uma descrição para a sua Empresa!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Introduza a localidade da sua Empresa!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Escolha a quantidade de empregados!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Escolha a imagem da sua Empresa!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Selecione uma foto"), Configs.SELECAO_GALERIA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagem = null;

        try {

            switch (requestCode) {
                case Configs.SELECAO_GALERIA:
                    Uri localImagemSelecionada = data.getData();

                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        // Usar versão mais recente do código
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                    } else {
                        /// Usar versão mais antiga do código
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Erro ao Recuperar Imagem!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // COMECA TUDO AQUI
        if (imagem != null) {
            imageViewEmpresa.setImageBitmap(imagem);

            // Recuperar Dados da Imagem para o Firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, 100, baos);
            dadosImagem = baos.toByteArray();
        }

    }

    private void buscarEmpregados(Spinner spinnerEmpregados) {
        dialog.show();
        DatabaseReference referenciaTags = DefinicaoFirebase.recuperarBaseDados().child("empregados");
        referenciaTags.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEmpregados.add("Selecionar Empregados...");
                for (DataSnapshot dados : snapshot.getChildren()) {
                    listaEmpregados.add(dados.getValue(String.class));
                }
                ArrayAdapter<List<String>> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listaEmpregados);
                spinnerEmpregados.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}