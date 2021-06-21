package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Notificacao;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class EditarNotificacaoActivity extends AppCompatActivity {

    private ImageView imageViewEditarNotificacao;
    private EditText editTextTitulo, editTextDescricao;
    private AlertDialog dialogCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_notificacao);

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        String idNotificacao = getIntent().getStringExtra("idNotificacao");

        imageViewEditarNotificacao = findViewById(R.id.imageViewEditarNotificacao);

        editTextTitulo = findViewById(R.id.editTextTituloEditarNotificacao);
        editTextDescricao = findViewById(R.id.editTextDescricaoEditarNotificacao);

        Button buttonEditarNotificacao;
        buttonEditarNotificacao = findViewById(R.id.buttonEditarNotificacao);

        buttonEditarNotificacao.setOnClickListener(v -> {

            String titulo = editTextTitulo.getText().toString();
            String descricao = editTextDescricao.getText().toString();

            if (!titulo.isEmpty()) {
                if (!descricao.isEmpty()) {
                    dialog.show();
                    HashMap<String, Object> hashMapNotificacao = new HashMap<>();
                    hashMapNotificacao.put("titulo", titulo);
                    hashMapNotificacao.put("descricao", descricao);

                    DatabaseReference notificacaoRef = DefinicaoFirebase.recuperarBaseDados().child("notificacoes").child(idNotificacao);

                    notificacaoRef.updateChildren(hashMapNotificacao).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(EditarNotificacaoActivity.this, "Alterações guardadas com Sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditarNotificacaoActivity.this, "Introduza uma Descrição!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditarNotificacaoActivity.this, "Introduza um Título!", Toast.LENGTH_SHORT).show();
            }


        });

        buscarNotificacao(idNotificacao);

    }

    private void buscarNotificacao(String idNotificacao) {

        DatabaseReference notificacaoRef = DefinicaoFirebase.recuperarBaseDados().child("notificacoes").child(idNotificacao);

        notificacaoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialogCarregamento.show();
                Notificacao notificacao = snapshot.getValue(Notificacao.class);

                Glide.with(getApplicationContext()).load(notificacao.getImagem()).into(imageViewEditarNotificacao);
                editTextTitulo.setText(notificacao.getTitulo());
                editTextDescricao.setText(notificacao.getDescricao());
                dialogCarregamento.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

}