package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Post;
import com.josexavier.code4all.interfaces.Validacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class EditarPostActivity extends AppCompatActivity {

    private ImageView imageViewEditarPost;
    private EditText titulo, descricao;
    private Spinner spinnerTags;

    private Post post = new Post();
    private List<String> tags = new ArrayList<>();

    private android.app.AlertDialog dialog;
    private AlertDialog dialogCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_post);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        imageViewEditarPost = findViewById(R.id.imageViewEditarPost);

        titulo = findViewById(R.id.editTextTituloEditarPost);
        descricao = findViewById(R.id.editTextDescricaoEditarPost);

        spinnerTags = findViewById(R.id.spinnerTagsEditarPost);

        String idPost = getIntent().getStringExtra("idPost");
        buscarPost(idPost, validar -> {
            if (validar) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext()).load(post.getImagem()).into(imageViewEditarPost);
                titulo.setText(post.getTitulo());
                descricao.setText(post.getDescricao());
                buscarTags(post.getTag());
            }
        });

    }

    private void buscarTags(String tag) {
        tags.add(tag);
        DatabaseReference referenciaTags = DefinicaoFirebase.recuperarBaseDados().child("tags");
        referenciaTags.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (!dados.getValue(String.class).equals(tag)) { // Se for diferente do já anteriormente selecionado.
                        tags.add(dados.getValue(String.class));
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        ArrayAdapter<List<String>> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, tags);
        spinnerTags.setAdapter(adapter);
    }

    private void buscarPost(String idPost, Validacao validacao) {
        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                post = snapshot.getValue(Post.class);
                validacao.isValidacaoSucesso(true);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void confirmarEdicaoPost(View view) {
        String tTitulo = titulo.getText().toString();
        String tDescricao = descricao.getText().toString();
        int posicao = spinnerTags.getSelectedItemPosition();

        if (!tTitulo.isEmpty()) {
            if (!tDescricao.isEmpty()) {
                atualizarPost(tTitulo, tDescricao, posicao);
            } else {
                Toast.makeText(this, "Introduza uma Descrição!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Introduza um Título!", Toast.LENGTH_SHORT).show();
        }

    }

    private void atualizarPost(String tTitulo, String tDescricao, int posicao) {
        try {
            dialogCarregamento.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, Object> hashMapAtualizarPost = new HashMap<>();
        hashMapAtualizarPost.put("titulo", tTitulo);
        hashMapAtualizarPost.put("descricao", tDescricao);
        hashMapAtualizarPost.put("tag", tags.get(posicao));

        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(post.getId());
        postRef.updateChildren(hashMapAtualizarPost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialogCarregamento.dismiss();
                Toast.makeText(EditarPostActivity.this, "Post Atualizado com Sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                dialogCarregamento.dismiss();
                Toast.makeText(EditarPostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
            }
        });
    }

}