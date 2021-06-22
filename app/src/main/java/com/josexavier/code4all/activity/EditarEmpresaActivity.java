package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Empresa;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class EditarEmpresaActivity extends AppCompatActivity {

    private LinearLayout linearLayoutEmpresa;
    private ImageView imageViewEmpresa;
    private EditText editTextNome, editTextEmail, editTextDescricao;

    private Bitmap imagem = null;
    private byte[] dadosImagem;
    private AlertDialog dialogCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empresa);

        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        configuracoesIniciais();

        Configs.recuperarIdUtilizador(idUtilizador -> {
            buscarInfo(idUtilizador);
        });


    }

    private void configuracoesIniciais() {
        linearLayoutEmpresa = findViewById(R.id.linearCorFundoEditarEmpresa);
        imageViewEmpresa = findViewById(R.id.imageViewEditarEmpresa);
        editTextNome = findViewById(R.id.editTextNomeEditarEmpresa);
        editTextEmail = findViewById(R.id.editTextEmailEditarEmpresa);
        editTextDescricao = findViewById(R.id.editTextDescricaoEditarEmpresa);

        ImageButton imageButtonGaleria;

        imageButtonGaleria = findViewById(R.id.imageButtonGaleriaEditarEmpresa);
        imageButtonGaleria.setOnClickListener(v -> Configs.abrirGaleria(this));
    }

    private void buscarInfo(String idUtilizador) {

        DatabaseReference empresaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialogCarregamento.show();
                Empresa empresa = snapshot.getValue(Empresa.class);

                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.borda_preta);
                drawable.setColorFilter(empresa.getCorFundoPerfil(), PorterDuff.Mode.SRC);
                linearLayoutEmpresa.setBackground(drawable);

                Glide.with(getApplicationContext()).load(empresa.getFoto()).into(imageViewEmpresa);
                editTextNome.setText(empresa.getNome());
                editTextEmail.setText(empresa.getEmail());
                editTextDescricao.setText(empresa.getDescricao());
                dialogCarregamento.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagem = null;

        try {

            switch (requestCode) {
                case Configs.SELECAO_CAMARA:
                    imagem = (Bitmap) data.getExtras().get("data");
                    break;
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

        if (imagem != null) {
            imageViewEmpresa.setImageBitmap(imagem);

            // Recuperar Dados da Imagem para o Firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, 100, baos);
            dadosImagem = baos.toByteArray();
        }

    }

    public void atualizarCorFundoEmpresa(View view) {
        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Atualizando a Cor de Fundo da Empresa...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialog.show();
        Configs.recuperarIdUtilizador(idUtilizador -> {
            DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);

            HashMap<String, Object> corPerfil = new HashMap<>();
            int corAleatoria = Configs.corAleatoria();
            corPerfil.put("corFundoPerfil", corAleatoria);

            utilizadorRef.updateChildren(corPerfil).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Cor de Fundo atualizada com Sucesso!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            });
        });

    }

    private void guardarFoto(Validacao validacao) { // Guardar foto no FirebaseStorage, guarda foto no perfil do utilizador e guarda também na FirebaseDatabase (url da foto)
        FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();

        StorageReference firebaseStorage = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("perfil").child(autenticacao.getCurrentUser().getUid()).child("foto.png");
        UploadTask uploadTask = firebaseStorage.putBytes(dadosImagem);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseStorage.getDownloadUrl().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        final String foto = task2.getResult().toString();

                        UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setPhotoUri(task2.getResult()).build();
                        FirebaseUser utilizador = autenticacao.getCurrentUser();

                        utilizador.updateProfile(profileRequest).addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful()) {
                                HashMap<String, Object> fotoPerfil = new HashMap<>();
                                fotoPerfil.put("foto", foto);

                                DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(utilizador.getUid());

                                utilizadorRef.updateChildren(fotoPerfil).addOnCompleteListener(task4 -> {
                                    if (task4.isSuccessful())
                                        validacao.isValidacaoSucesso(true);
                                    else
                                        validacao.isValidacaoSucesso(false);

                                });

                            } else {
                                Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            } else
                Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
        });

    }

    public void confirmarAlteracoesEmpresa(View view) {
        String nome = editTextNome.getText().toString();
        String descricao = editTextDescricao.getText().toString();

        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
                dialog.show();
                Configs.recuperarIdUtilizador(idUtilizador -> {

                    DatabaseReference empresaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);

                    HashMap<String, Object> hashMapEditarEmpresa = new HashMap<>();
                    hashMapEditarEmpresa.put("nome", nome);
                    hashMapEditarEmpresa.put("descricao", descricao);
                    hashMapEditarEmpresa.put("dataDescricao", Configs.recuperarDataHoje());

                    empresaRef.updateChildren(hashMapEditarEmpresa).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (dadosImagem != null) { // Guarda imagem e depois o nome
                                guardarFoto(sucesso -> {
                                    if (sucesso) {
                                        Configs.guardarNome(nome, nomeGuardado -> {
                                            if (nomeGuardado) {
                                                dialog.dismiss();
                                                Toast.makeText(EditarEmpresaActivity.this, "Alterações guardadas com Sucesso!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else
                                                Toast.makeText(EditarEmpresaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                        });
                                    } else
                                        Toast.makeText(EditarEmpresaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                });
                            } else { // Guarda nome apenas
                                Configs.guardarNome(nome, nomeGuardado -> {
                                    if (nomeGuardado) {
                                        dialog.dismiss();
                                        Toast.makeText(EditarEmpresaActivity.this, "Alterações guardadas com Sucesso!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else
                                        Toast.makeText(EditarEmpresaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                });
                            }

                        } else
                            Toast.makeText(EditarEmpresaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    });

                });


            } else {
                Toast.makeText(this, "Introduza uma Descrição!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Introduza um Nome!", Toast.LENGTH_SHORT).show();
        }

    }
}