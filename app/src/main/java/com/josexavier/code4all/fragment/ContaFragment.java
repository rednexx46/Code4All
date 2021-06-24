package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.EditarPerfilActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Conta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;

public class ContaFragment extends Fragment {

    private Conta conta = new Conta();
    private LinearLayout fundoPerfil;
    private ImageView fotoPerfil;
    private TextView nomeConta, biografia, biografiaData, contaXP, estado, nascimento;
    private boolean jaDeuParabens = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_conta, container, false);

        Configs.recuperarIdUtilizador(idUtilizador -> recuperarDadosUtilizador(idUtilizador));

        fundoPerfil = root.findViewById(R.id.linearLayoutConta);
        fotoPerfil = root.findViewById(R.id.imageContaPerfil);
        nomeConta = root.findViewById(R.id.textContaNome);
        biografia = root.findViewById(R.id.textContaBiografia);
        biografiaData = root.findViewById(R.id.textContaBiografiaData);
        contaXP = root.findViewById(R.id.textContaXP);
        estado = root.findViewById(R.id.textContaEstado);
        nascimento = root.findViewById(R.id.textContaNascimento);

        Button botaoEditarPerfil;
        botaoEditarPerfil = root.findViewById(R.id.buttonEditarPerfil);
        botaoEditarPerfil.setOnClickListener(v -> abrirEditarPerfil());

        return root;
    }

    private void abrirEditarPerfil() {
        Intent atividadeEditarPerfil = new Intent(getContext(), EditarPerfilActivity.class);
        atividadeEditarPerfil.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(atividadeEditarPerfil);
    }

    private void recuperarDadosUtilizador(String idUtilizador) {
        AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialog.show();
        Log.i("dados", "idUtilizador .: " + idUtilizador);
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);

        utilizadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conta = snapshot.getValue(Conta.class);

                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.borda_preta);
                drawable.setColorFilter(conta.getCorFundoPerfil(), PorterDuff.Mode.SRC);

                fundoPerfil.setBackground(drawable);
                Glide.with(getContext()).load(conta.getFoto()).into(fotoPerfil);
                nomeConta.setText(conta.getNome());
                contaXP.setText("Total de XP .: " + conta.getTotalXP());
                biografia.setText(conta.getBiografia());
                biografiaData.setText("Última vez atualizado .: " + conta.getBiografiaData());
                estado.setText("Estado .: " + conta.getEstado());
                nascimento.setText("Data de Nascimento\n" + conta.getDataNascimento());

                SimpleDateFormat dataFormatacao = new SimpleDateFormat("dd/MM/yy");

                if (!jaDeuParabens) {
                    try {

                        Calendar calendarioHoje = Calendar.getInstance();
                        calendarioHoje.setTime(dataFormatacao.parse(Configs.recuperarDataHoje()));

                        Calendar calendarioNascimento = Calendar.getInstance();
                        calendarioNascimento.setTime(dataFormatacao.parse(conta.getDataNascimento()));

                        if ((calendarioHoje.get(Calendar.DAY_OF_MONTH) == calendarioNascimento.get(Calendar.DAY_OF_MONTH)) && (calendarioHoje.get(Calendar.MONTH) == calendarioNascimento.get(Calendar.MONTH)))
                            Toast.makeText(getContext(), "Parabéns, a Equipa Code4All deseja-lhe um ótimo dia, cheio de alegrias!", Toast.LENGTH_LONG).show();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    jaDeuParabens = true;
                }

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Configs.recuperarIdUtilizador(idUtilizador -> recuperarDadosUtilizador(idUtilizador));
    }
}