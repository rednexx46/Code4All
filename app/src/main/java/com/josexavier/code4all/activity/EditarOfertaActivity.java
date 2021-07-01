package com.josexavier.code4all.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Oferta;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EditarOfertaActivity extends AppCompatActivity {

    private TextView editTextTitulo, editTextDescricao, editTextMensagem;
    private String idOferta;
    private android.app.AlertDialog dialog, dialogCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_oferta);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();


        idOferta = getIntent().getStringExtra("idOferta");

        editTextTitulo = findViewById(R.id.editTextTituloEditarOferta);
        editTextDescricao = findViewById(R.id.editTextDescricaoEditarOferta);
        editTextMensagem = findViewById(R.id.editTextMensagemEditarOferta);

        buscarOferta();

    }

    private void buscarOferta() {
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseReference ofertaRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(idOferta);
        ofertaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Oferta oferta = snapshot.getValue(Oferta.class);

                editTextTitulo.setText(Objects.requireNonNull(oferta).getTitulo());
                editTextDescricao.setText(oferta.getDescricao());
                editTextMensagem.setText(oferta.getMensagem());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void confirmarAlteracoesOferta(View view) {
        String titulo = editTextTitulo.getText().toString();
        String descricao = editTextDescricao.getText().toString();
        String mensagem = editTextMensagem.getText().toString();

        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!mensagem.isEmpty()) {
                    try {
                        dialogCarregamento.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DatabaseReference ofertaRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(idOferta);
                    HashMap<String, Object> hashMapEditarOferta = new HashMap<>();
                    hashMapEditarOferta.put("titulo", titulo);
                    hashMapEditarOferta.put("descricao", descricao);
                    hashMapEditarOferta.put("mensagem", mensagem);
                    ofertaRef.updateChildren(hashMapEditarOferta).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogCarregamento.dismiss();
                            Toast.makeText(this, "Alterações realizadas com Sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            dialogCarregamento.dismiss();
                            Toast.makeText(EditarOfertaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Introduza uma Mensagem!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Introduza uma Descrição!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Introduza um Título!", Toast.LENGTH_SHORT).show();
        }

    }
}