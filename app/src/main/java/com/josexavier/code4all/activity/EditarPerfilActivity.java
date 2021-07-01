package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.josexavier.code4all.interfaces.Validacao;
import com.josexavier.code4all.model.Conta;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditarPerfilActivity extends AppCompatActivity {

    private LinearLayout linearLayoutCorFundoPerfil;
    private CircleImageView imagemPerfil;
    private EditText editTextNome, editTextEmail, editTextBiografia;
    private String idUtilizador = Configs.recuperarIdUtilizador();
    private AlertDialog dialog, dialogCarregamento;
    private ValueEventListener perfilListener;
    private DatabaseReference perfilRef;
    private boolean jaRecuperou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        linearLayoutCorFundoPerfil = findViewById(R.id.linearCorFundoPerfil);
        imagemPerfil = findViewById(R.id.imageContaEditarPerfil);
        editTextNome = findViewById(R.id.editTextEditarPerfilNome);
        editTextEmail = findViewById(R.id.editTextEditarPerfilEmail);
        editTextBiografia = findViewById(R.id.editTextEditarPerfilBiografia);

        recuperarDados();

    }

    private void recuperarDados() {
        perfilRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        perfilListener = perfilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialogCarregamento.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Conta conta = snapshot.getValue(Conta.class);

                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.borda_preta);
                Objects.requireNonNull(drawable).setColorFilter(Objects.requireNonNull(conta).getCorFundoPerfil(), PorterDuff.Mode.SRC);

                linearLayoutCorFundoPerfil.setBackground(drawable);
                Glide.with(getApplicationContext()).load(conta.getFoto()).into(imagemPerfil);
                if (!jaRecuperou) {
                    editTextNome.setText(Configs.recuperarNomeUtilizador());
                    editTextBiografia.setText(conta.getBiografia());
                    editTextEmail.setText(Configs.recuperarEmailUtilizador());
                    editTextEmail.setEnabled(false);
                    jaRecuperou = true;
                }
                dialogCarregamento.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        perfilRef.removeEventListener(perfilListener);
    }
    /*
    public void atualizarCorPerfil(View view) {
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        HashMap<String, Object> corPerfil = new HashMap<>();
        int corAleatoria = Configs.corAleatoria();
        corPerfil.put("corPerfil", corAleatoria);
        utilizadorRef.updateChildren(corPerfil).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                guardarFoto(corAleatoria, validar -> {
                    if (validar) {
                        dialog.dismiss();
                        Toast.makeText(EditarPerfilActivity.this, "Cor Atualizada com Sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(EditarPerfilActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    }
                });
            else {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
            }
        });
    }
     */

    public void atualizarCorFundoPerfil(View view) { // mudar a cor do botao para a cor gerada
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        HashMap<String, Object> corPerfil = new HashMap<>();
        corPerfil.put("corFundoPerfil", Configs.corAleatoria());
        utilizadorRef.updateChildren(corPerfil).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(getApplicationContext(), "Cor do Fundo de Perfil atualizada com Sucesso!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void guardarFoto(int corPerfil, Validacao validacao) { // Verificar apenas dois nomes e caracteres especiais
        String nome = editTextNome.getText().toString();
        if (!nome.isEmpty()) {
            FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();
            String iniciais = "";
            char c = nome.toUpperCase().charAt(0);
            iniciais += c;
            for (int i = 0; i < nome.length(); i++) {
                char letra = nome.toUpperCase().charAt(i);
                if (letra == ' ') {
                    iniciais += nome.toUpperCase().charAt(i + 1);
                }
            }

            if (iniciais.length() < 3 && iniciais.length() > 0) {

                Bitmap bitmap = Configs.desenharTexto(iniciais, 10000, corPerfil);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                StorageReference firebaseStorage = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("perfil").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid()).child("foto.jpeg");
                UploadTask uploadTask = firebaseStorage.putBytes(data);

                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseStorage.getDownloadUrl().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                final String foto = Objects.requireNonNull(task2.getResult()).toString();

                                UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setPhotoUri(task2.getResult()).build();
                                FirebaseUser utilizador = autenticacao.getCurrentUser();

                                utilizador.updateProfile(profileRequest).addOnCompleteListener(task3 -> {
                                    if (task3.isSuccessful()) {
                                        HashMap<String, Object> fotoPerfil = new HashMap<>();
                                        fotoPerfil.put("foto", foto);

                                        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);

                                        utilizadorRef.updateChildren(fotoPerfil).addOnCompleteListener(task4 -> {
                                            if (task4.isSuccessful()) {
                                                validacao.isValidacaoSucesso(true);
                                            } else {
                                                dialog.dismiss();
                                                validacao.isValidacaoSucesso(false);
                                                Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                            }

                                        });

                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                });
            } else {
                dialog.dismiss();
                Toast.makeText(this, "Introduza apenas o Primeiro e o Último Nome!", Toast.LENGTH_SHORT).show();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(this, "Introduza um nome!", Toast.LENGTH_SHORT).show();
        }

    }

    public void confirmarAlteracoesPerfil(View view) {
        String nome = editTextNome.getText().toString();
        String biografia = editTextBiografia.getText().toString();

        if (!nome.isEmpty()) {
            if (!biografia.isEmpty()) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                atualizarDadosPerfil();
            } else {
                dialog.dismiss();
                Toast.makeText(this, "Introduza algo na Biografia!", Toast.LENGTH_SHORT).show();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(this, "Introduza um Nome!", Toast.LENGTH_SHORT).show();
        }

    }

    private void guardarNome(String nome, Validacao validacao) {

        UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        FirebaseUser utilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser();
        Objects.requireNonNull(utilizador).updateProfile(profileRequest).addOnCompleteListener(task -> {
            validacao.isValidacaoSucesso(task.isSuccessful());
        });

    }

    private void atualizarDadosPerfil() {
        DatabaseReference contaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);

        String nome, biografia;
        nome = editTextNome.getText().toString();
        biografia = editTextBiografia.getText().toString();

        if (!nome.isEmpty()) {
            if (!biografia.isEmpty()) {
                contaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Conta conta = snapshot.getValue(Conta.class);
                        guardarFoto(Objects.requireNonNull(conta).getCorPerfil(), validar -> {
                            if (validar) {
                                HashMap<String, Object> dadosPerfil = new HashMap<>();
                                dadosPerfil.put("nome", nome);
                                dadosPerfil.put("biografia", biografia);
                                dadosPerfil.put("biografiaData", Configs.recuperarDataHoje());

                                contaRef.updateChildren(dadosPerfil).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        guardarNome(nome, validar2 -> {
                                            if (validar2) {
                                                dialog.dismiss();
                                                Toast.makeText(EditarPerfilActivity.this, "Dados Guardados com Sucesso!", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            } else
                                                Toast.makeText(EditarPerfilActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        });
                                    } else {
                                        Toast.makeText(EditarPerfilActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else
                                Toast.makeText(EditarPerfilActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(this, "Introduza uma biografia!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Introduza um Nome!", Toast.LENGTH_SHORT).show();
        }

    }

}