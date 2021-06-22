package com.josexavier.code4all.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.adapter.NotificacoesAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Notificacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NotificacoesActivity extends AppCompatActivity {

    private List<Notificacao> listaNotificacoes = new ArrayList<>();
    private NotificacoesAdapter notificacoesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        RecyclerView recyclerViewNotificacoes;
        recyclerViewNotificacoes = findViewById(R.id.recyclerViewNotificacoes);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewNotificacoes.getContext(), RecyclerView.VERTICAL);

        recyclerViewNotificacoes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        recyclerViewNotificacoes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewNotificacoes.addItemDecoration(dividerItemDecoration);
        recyclerViewNotificacoes.setHasFixedSize(true);

        notificacoesAdapter = new NotificacoesAdapter(listaNotificacoes, getApplicationContext());
        recyclerViewNotificacoes.setAdapter(notificacoesAdapter);

        buscarNotificacoes();

    }

    private void buscarNotificacoes() {
        DatabaseReference notificacoesRef = DefinicaoFirebase.recuperarBaseDados().child("notificacoes");

        notificacoesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaNotificacoes.clear();

                for (DataSnapshot dados : snapshot.getChildren())
                    listaNotificacoes.add(dados.getValue(Notificacao.class));

                notificacoesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}