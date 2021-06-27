package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class CriarPostFragment extends Fragment {

    private Bitmap imagem = null;
    private byte[] dadosImagem;
    private List<String> tags = new ArrayList<>();
    private ImageView imagemPost;
    private AlertDialog dialog, dialogCarregamento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_criar_post, container, false);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Criando Post...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        EditText editTextTitulo, editTextDescricao;
        Spinner spinnerTags;
        Button buttonCriarPostAdmin;
        ImageButton imageButtonGaleria;

        editTextTitulo = root.findViewById(R.id.editTextCriarPostAdminTitulo);
        editTextDescricao = root.findViewById(R.id.editTextCriarPostAdminDescricao);
        spinnerTags = root.findViewById(R.id.spinnerCriarPostAdminTags);
        buttonCriarPostAdmin = root.findViewById(R.id.buttonCriarPostAdmin);
        imagemPost = root.findViewById(R.id.imageCriarPostAdminImagem);


        imageButtonGaleria = root.findViewById(R.id.imageCriarPostAdminGaleria);
        imageButtonGaleria.setOnClickListener(view -> abrirGaleria());

        buttonCriarPostAdmin.setOnClickListener(v -> {

            String titulo = editTextTitulo.getText().toString();
            String descricao = editTextDescricao.getText().toString();
            int tag = spinnerTags.getSelectedItemPosition();

            verificarCampos(titulo, descricao, tag);
        });

        buscarTags(spinnerTags);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagem = null;

        try {

            switch (requestCode) {
                case Configs.SELECAO_GALERIA:
                    Uri localImagemSelecionada = data.getData();

                    if (Build.VERSION.SDK_INT >= 29) {
                        // Usar versão mais recente do código
                        ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                    } else {
                        /// Usar versão mais antiga do código
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao Recuperar Imagem!" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Selecione uma foto"), Configs.SELECAO_GALERIA);
        }
    }

    private void criarPost(String titulo, String descricao, int posicao) {
        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts");

        Post post = new Post();

        post.setId(postRef.push().getKey());
        post.setCriador(Configs.recuperarNomeUtilizador());
        post.setDataCriacao(Configs.recuperarDataHoje());
        post.setTitulo(titulo);
        post.setEstado(Configs.ACEITE);
        post.setDescricao(descricao);
        post.setTag(tags.get(posicao));
        post.setIdCriador(Configs.recuperarIdUtilizador());
        post.guardarImagem(dadosImagem, validar -> {
            if (validar) {
                post.guardar(validar2 -> {
                    if (validar2) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Post Criado com Sucesso!", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Escolha uma Imagem para o Post!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Selecione uma Tag!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Introduza uma Descrição para o Post!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Introduza um Título para o Post!", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<List<String>> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, tags);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}