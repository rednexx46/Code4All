package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Conta;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ContaActivity extends AppCompatActivity {

    private LinearLayout fundoPerfil;
    private CircleImageView imagemPerfil;
    private TextView nomePerfil, contaXP, biografia, biografiaData, estado, nascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        String idConta = getIntent().getStringExtra("idConta");

        fundoPerfil = findViewById(R.id.linearLayoutUtilizadorConta);

        imagemPerfil = findViewById(R.id.imageContaUtilizadorPerfil);

        nomePerfil = findViewById(R.id.textContaUtilizadorNome);
        contaXP = findViewById(R.id.textUtilizadorContaXP);
        biografia = findViewById(R.id.textUtilizadorContaBiografia);
        biografiaData = findViewById(R.id.textUtilizadorContaBiografiaData);
        estado = findViewById(R.id.textEstadoUtilizador);
        nascimento = findViewById(R.id.textNascimentoUtilizador);

        buscarConta(idConta);

    }

    private void buscarConta(String idConta) {
        DatabaseReference contaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idConta);
        AlertDialog dialog = new SpotsDialog.Builder().setContext(ContaActivity.this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        contaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialog.show();
                Conta conta = snapshot.getValue(Conta.class);

                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.borda_preta);
                drawable.setColorFilter(conta.getCorFundoPerfil(), PorterDuff.Mode.SRC);

                fundoPerfil.setBackground(drawable);

                Glide.with(getApplicationContext()).load(conta.getFoto()).into(imagemPerfil);
                nomePerfil.setText(conta.getNome());
                contaXP.setText("Total de XP .: " + conta.getTotalXP());
                biografia.setText(conta.getBiografia());
                biografiaData.setText("Última vez atualizado .: " + conta.getBiografiaData());
                estado.setText("Estado .: " + conta.getEstado());
                nascimento.setText("Data de Nascimento\n" + conta.getDataNascimento());

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}