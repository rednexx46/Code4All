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
import com.josexavier.code4all.model.Post;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CriarPostActivity extends AppCompatActivity {

    private final int SELECAO_CAMARA = 100;
    private final int SELECAO_GALERIA = 200;

    private List<String> tags = new ArrayList<>();
    private ImageView imagemPost;

    private Bitmap imagem = null;
    private byte[] dadosImagem;
    private AlertDialog dialog, dialogCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_post);

        imagemPost = findViewById(R.id.imageCriarPostImagem);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Post...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        ImageButton imageButtonGaleria;
        imageButtonGaleria = findViewById(R.id.imageCriarPostGaleria);
        imageButtonGaleria.setOnClickListener(view -> abrirGaleria());

        EditText editTextTitulo, editTextDescricao;
        editTextTitulo = findViewById(R.id.editTextCriarPostTitulo);
        editTextDescricao = findViewById(R.id.editTextCriarPostDescricao);

        Spinner spinnerTags;
        spinnerTags = findViewById(R.id.spinnerCriarPostTags);
        buscarTags(spinnerTags);

        Button buttonCriarPost;
        buttonCriarPost = findViewById(R.id.buttonCriarPost);

        buttonCriarPost.setOnClickListener(v -> {

            String titulo = editTextTitulo.getText().toString();
            String descricao = editTextDescricao.getText().toString();
            int tag = spinnerTags.getSelectedItemPosition();

            verificarCampos(titulo, descricao, tag);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagem = null;

        try {

            switch (requestCode) {
                case SELECAO_CAMARA:
                    imagem = (Bitmap) data.getExtras().get("data");
                    break;
                case SELECAO_GALERIA:
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
            imagemPost.setImageBitmap(imagem);

            // Recuperar Dados da Imagem para o Firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, 100, baos);
            dadosImagem = baos.toByteArray();
        }

    }

    private void criarPost(String titulo, String descricao, int posicao) {
        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts");

        Post post = new Post();

        post.setId(postRef.push().getKey());
        post.setCriador(Configs.recuperarNomeUtilizador());
        post.setDataCriacao(Configs.recuperarDataHoje());
        post.setTitulo(titulo);
        post.setEstado(Configs.PENDENTE); // alterado
        post.setDescricao(descricao);
        post.setTag(tags.get(posicao));
        post.setIdCriador(Configs.recuperarIdUtilizador());
        post.guardarImagem(dadosImagem, validar -> {
            if (validar) {
                post.guardar(validar2 -> {
                    if (validar2) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Post Criado com Sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarCampos(String titulo, String descricao, int tag) {
        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (tag > 0) {
                    if (imagem != null) {
                        dialog.show();
                        criarPost(titulo, descricao, tag);
                    } else {
                        Toast.makeText(getApplicationContext(), "Escolha uma Imagem para o Post!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Selecione uma Tag!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Introduza uma Descrição para o Post!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Introduza um Título para o Post!", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, SELECAO_GALERIA);
        }
    }

    private void buscarTags(Spinner spinner) {
        dialogCarregamento.show();
        DatabaseReference referenciaTags = DefinicaoFirebase.recuperarBaseDados().child("tags");
        referenciaTags.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tags.add("Selecionar Tag...");
                for (DataSnapshot dados : snapshot.getChildren()) {
                    tags.add(dados.getValue(String.class));
                }
                dialogCarregamento.dismiss();
                ArrayAdapter<List<String>> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, tags);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}