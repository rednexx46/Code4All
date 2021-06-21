package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Oferta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import dmax.dialog.SpotsDialog;

public class CriarOfertaActivity extends AppCompatActivity {

    private String nome, foto;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_oferta);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Criando Oferta...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        String idConta = getIntent().getStringExtra("idConta");

        TextView textViewSlogan;
        textViewSlogan = findViewById(R.id.textViewSloganCriarOferta);

        EditText editTextTitulo, editTextDescricao, editTextMensagem;
        editTextTitulo = findViewById(R.id.editTextTituloCriarOferta);
        editTextDescricao = findViewById(R.id.editTextDescricaoCriarOferta);
        editTextMensagem = findViewById(R.id.editTextMensagemCriarOferta);

        Button buttonCriarOferta;
        buttonCriarOferta = findViewById(R.id.buttonCriarOferta);

        buscarNomeFotoUtilizador(textViewSlogan, idConta, validar -> {
            if (validar) {
                buttonCriarOferta.setOnClickListener(v -> {
                    String titulo = editTextTitulo.getText().toString();
                    String descricao = editTextDescricao.getText().toString();
                    String mensagem = editTextMensagem.getText().toString();

                    if (!titulo.isEmpty()) {
                        if (!descricao.isEmpty()) {
                            if (!mensagem.isEmpty()) {

                                dialog.show();

                                DatabaseReference ofertasRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas");
                                String idOferta = ofertasRef.push().getKey();

                                Oferta oferta = new Oferta();
                                oferta.setId(idOferta);
                                oferta.setEmailUtilizador(Configs.recuperarEmailUtilizador());
                                oferta.setNomeEmpresa(Configs.recuperarNomeUtilizador());
                                oferta.setTitulo(titulo);
                                oferta.setDescricao(descricao);
                                oferta.setMensagem(mensagem);
                                oferta.setDataEnvio(Configs.recuperarDataHoje());
                                oferta.setIdUtilizador(idConta);
                                oferta.setEstado(Configs.PENDENTE);
                                oferta.setNomeUtilizador(nome);
                                oferta.setIdEmpresa(Configs.recuperarIdUtilizador());
                                oferta.setFotoEmpresa(Configs.recuperarFotoUtilizador());
                                oferta.setFotoUtilizador(foto);

                                ofertasRef.child(idOferta).setValue(oferta).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(CriarOfertaActivity.this, "Oferta criada com Sucesso!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(CriarOfertaActivity.this, getApplicationContext().getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Introduza uma Mensagem!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Introduza uma Descrição!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Introduza um Título!", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

    }

    private void buscarNomeFotoUtilizador(TextView textViewSlogan, String idConta, Validacao isSucesso) {
        DatabaseReference contaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idConta);
        contaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                nome = snapshot.child("nome").getValue(String.class);
                foto = snapshot.child("foto").getValue(String.class);
                textViewSlogan.setText("Está atualmente a criar uma oferta para o utilizador " + nome + "!");
                isSucesso.isValidacaoSucesso(true);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}