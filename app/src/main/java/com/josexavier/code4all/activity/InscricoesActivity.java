package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class InscricoesActivity extends AppCompatActivity {

    private ListView inscricoes;

    private ArrayAdapter<String> listaInscricoesAdapter;

    private ArrayList<String> listaInscricoes = new ArrayList<>();
    private ArrayList<String> idInscricoes = new ArrayList<>();

    private ArrayList<String> inscricoesUtilizador = new ArrayList<>();
    private ArrayList<String> idInscricoesUtilizador = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscricoes);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();


        inscricoes = findViewById(R.id.listViewInscricoes);
        inscricoes.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listaInscricoesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, listaInscricoes);
        inscricoes.setAdapter(listaInscricoesAdapter);

        inscricoes.setOnItemClickListener((parent, view, position, id) -> {

            SparseBooleanArray inscricoesSelecionadas = inscricoes.getCheckedItemPositions();

            for (int i = 0; i < listaInscricoes.size(); i++) {
                if (inscricoesSelecionadas.get(i)) {
                    inscricoesUtilizador.add(listaInscricoes.get(position));
                    idInscricoesUtilizador.add(idInscricoes.get(position));
                }
            }

        });

        buscarInscricoes();

    }

    public void guardarInscricoes(View view) {
        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Inscrições...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String idUtilizador = Configs.recuperarIdUtilizador();
        DatabaseReference inscricoesRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador).child("temas");
        HashMap<String, String> inscricoesFinal = new HashMap<>();

        for (int i = 0; i < inscricoesUtilizador.size(); i++) {
            inscricoesFinal.put(idInscricoesUtilizador.get(i), inscricoesUtilizador.get(i));
        }

        inscricoesRef.setValue(inscricoesFinal).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                Toast.makeText(InscricoesActivity.this, "Inscrições Guardadas com Sucesso!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            } else {
                dialog.dismiss();
                Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void buscarInscricoes() {
        DatabaseReference inscricoesRef = DefinicaoFirebase.recuperarBaseDados().child("temas");
        inscricoesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (DataSnapshot dados : snapshot.getChildren()) {
                    idInscricoes.add(dados.getKey());
                    listaInscricoes.add(dados.getValue(String.class));
                }
                dialog.dismiss();
                listaInscricoesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void limparInscricoes(View view) {
        for (int i = 0; i < listaInscricoes.size(); i++) {
            inscricoes.setItemChecked(i, false);
        }
        inscricoesUtilizador.clear();
        idInscricoesUtilizador.clear();
        Toast.makeText(this, "Inscrições Limpas com Sucesso!", Toast.LENGTH_SHORT).show();
    }
}