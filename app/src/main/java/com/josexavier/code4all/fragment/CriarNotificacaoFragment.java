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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Notificacao;

import java.io.ByteArrayOutputStream;

import dmax.dialog.SpotsDialog;

public class CriarNotificacaoFragment extends Fragment {

    private Bitmap imagem = null;
    private byte[] dadosImagem;
    private EditText editTextTitulo, editTextDescricao;

    private AlertDialog dialogCarregamento;

    private ImageView imageViewCriarQuiz;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_criar_notificacao, container, false);

        dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Criando Notificação...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        imageViewCriarQuiz = root.findViewById(R.id.imageViewCriarNotificacao);

        editTextTitulo = root.findViewById(R.id.editTextTituloCriarNotificacao);
        editTextDescricao = root.findViewById(R.id.editTextDescricaoCriarNotificacao);

        ImageButton imageButtonGaleria;

        imageButtonGaleria = root.findViewById(R.id.imageButtonGaleriaCriarNotificacao);
        imageButtonGaleria.setOnClickListener(view -> abrirGaleria());

        Button buttonCriarNotificacao;
        buttonCriarNotificacao = root.findViewById(R.id.buttonCriarNotificacao);

        buttonCriarNotificacao.setOnClickListener(v -> {
            String titulo = editTextTitulo.getText().toString();
            String descricao = editTextDescricao.getText().toString();

            if (!titulo.isEmpty()) {
                if (!descricao.isEmpty()) {
                    if (imagem != null) {
                        try {
                            dialogCarregamento.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Notificacao notificacao = new Notificacao();
                        DatabaseReference notificacaoRef = DefinicaoFirebase.recuperarBaseDados().child("notificacoes");
                        String idNotificacao = notificacaoRef.push().getKey();

                        notificacao.setId(idNotificacao);
                        notificacao.setTitulo(titulo);
                        notificacao.setDescricao(descricao);
                        notificacao.setData(Configs.recuperarDataHoje());

                        notificacaoRef.child(idNotificacao).setValue(notificacao).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                notificacao.guardarImagem(dadosImagem, validar -> {
                                    if (validar) {
                                        notificacao.guardar(validar2 -> {
                                            if (validar2) {
                                                dialogCarregamento.dismiss();
                                                Toast.makeText(getContext(), "Notificação criada com Sucesso!", Toast.LENGTH_SHORT).show();
                                                getActivity().onBackPressed();
                                            } else {
                                                dialogCarregamento.dismiss();
                                                Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                            }

                                        });
                                    } else {
                                        dialogCarregamento.dismiss();
                                        Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                dialogCarregamento.dismiss();
                                Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "Escolha uma imagem!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Introduza uma Descrição!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Introduza um Titulo!", Toast.LENGTH_SHORT).show();
            }

        });


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
            imageViewCriarQuiz.setImageBitmap(imagem);

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

}