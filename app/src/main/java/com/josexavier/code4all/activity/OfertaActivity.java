package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Oferta;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class OfertaActivity extends AppCompatActivity {

    private CircleImageView imageViewOferta;
    private TextView textViewNome;
    private EditText editTextTitulo, editTextDescricao, editTextMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oferta);

        String idOferta = getIntent().getStringExtra("idOferta");

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        imageViewOferta = findViewById(R.id.circleImageViewOferta);
        textViewNome = findViewById(R.id.textViewNomeOferta);
        editTextTitulo = findViewById(R.id.editTextTituloOferta);
        editTextDescricao = findViewById(R.id.editTextDescricaoOferta);
        editTextMensagem = findViewById(R.id.editTextMensagemOferta);

        Configs.recuperarGrupo(grupo -> {
            dialog.show();
            if (grupo.equals("membro")) { // se for utilizador normal
                buscarOfertaUtilizador(idOferta, buscaSucesso -> {
                    if (buscaSucesso) {
                        // Configurações Iniciais
                        DatabaseReference ofertaRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(idOferta);

                        LinearLayout linearLayoutOferta = findViewById(R.id.linearLayoutEstadoOferta);
                        ImageButton buttonAceitarOferta, buttonRecusarOferta;

                        linearLayoutOferta.setVisibility(View.VISIBLE);
                        buttonAceitarOferta = findViewById(R.id.imageButtonAceitarOferta);
                        buttonRecusarOferta = findViewById(R.id.imageButtonRecusarOferta);

                        // Configuração dos métodos "onClick" dos botões para a oferta apenas aplicável se o utilizador for do tipo "membro"
                        buttonAceitarOferta.setOnClickListener(v -> aceitarOferta(ofertaRef, textViewNome.getText().toString()));
                        buttonRecusarOferta.setOnClickListener(v -> recusarOferta(ofertaRef, textViewNome.getText().toString()));
                        dialog.dismiss();
                    }
                });

            } else if (grupo.equals("empresa")) { // se for empresa
                buscarOfertaEmpresa(idOferta);
                dialog.dismiss();
            }
        });


    }

    private void aceitarOferta(DatabaseReference ofertaRef, String empresa) { // atualizar o estado da oferta para "aceite"
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        AlertDialog dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Aceitando Oferta...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        alertDialog.setTitle("Quer realmente aceitar esta Oferta?");
        alertDialog.setMessage("Ao realizar esta operação estará a ACEITAR uma Oferta formidável da empresa \"" + empresa + "\".");
        alertDialog.setPositiveButton("Sim", (dialog, which) -> {
            dialogCarregamento.show();
            HashMap<String, Object> hashMapOfertaEstado = new HashMap<>();
            hashMapOfertaEstado.put("estado", Configs.ACEITE);
            ofertaRef.updateChildren(hashMapOfertaEstado).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(OfertaActivity.this, "Oferta aceite com Sucesso, irá receber um email por parte da " + empresa + " brevemente!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    dialogCarregamento.dismiss();
                    finish();
                } else
                    Toast.makeText(OfertaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
            });
        });
        alertDialog.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    private void recusarOferta(DatabaseReference ofertaRef, String empresa) { // atualizar o estado da oferta para "recusado"
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        AlertDialog dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Recusando Oferta...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        alertDialog.setTitle("Quer realmente recusar esta Oferta?");
        alertDialog.setMessage("Ao realizar esta operação estará a RECUSAR uma Oferta formidável da empresa \"" + empresa + "\".");
        alertDialog.setPositiveButton("Sim", (dialog, which) -> {
            dialogCarregamento.show();
            HashMap<String, Object> hashMapOfertaEstado = new HashMap<>();
            hashMapOfertaEstado.put("estado", Configs.RECUSADO);
            ofertaRef.updateChildren(hashMapOfertaEstado).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(OfertaActivity.this, "Oferta recusada com Sucesso!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dialogCarregamento.dismiss();
                    finish();
                } else
                    Toast.makeText(OfertaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
            });
        });
        alertDialog.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    private void buscarOfertaEmpresa(String idOferta) {
        DatabaseReference ofertaRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(idOferta);
        ofertaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Oferta oferta = snapshot.getValue(Oferta.class);

                Glide.with(getApplicationContext()).load(oferta.getFotoUtilizador()).into(imageViewOferta);
                textViewNome.setText(oferta.getNomeUtilizador());
                editTextTitulo.setText(oferta.getTitulo());
                editTextDescricao.setText(oferta.getDescricao());
                editTextMensagem.setText(oferta.getMensagem());

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void buscarOfertaUtilizador(String idOferta, Validacao validacao) {
        DatabaseReference ofertaRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(idOferta);
        ofertaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Oferta oferta = snapshot.getValue(Oferta.class);

                Glide.with(getApplicationContext()).load(oferta.getFotoEmpresa()).into(imageViewOferta);
                textViewNome.setText(oferta.getNomeEmpresa());
                editTextTitulo.setText(oferta.getTitulo());
                editTextDescricao.setText(oferta.getDescricao());
                editTextMensagem.setText(oferta.getMensagem());

                validacao.isValidacaoSucesso(true);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}