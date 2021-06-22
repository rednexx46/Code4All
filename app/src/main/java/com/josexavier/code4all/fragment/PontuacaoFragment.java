package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.ContaActivity;
import com.josexavier.code4all.adapter.PontuacaoAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Conta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PontuacaoFragment extends Fragment {

    private List<Conta> listaContas = new ArrayList<>();
    private PontuacaoAdapter pontuacaoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pontuacao, container, false);

        // Definição do RecyclerViewPontuacao
        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewPontuacao);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent activityConta = new Intent(getContext(), ContaActivity.class);
                activityConta.putExtra("idConta", listaContas.get(position).getId());
                activityConta.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(activityConta);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        // Configuração Adapter
        pontuacaoAdapter = new PontuacaoAdapter(listaContas, getContext());
        recyclerView.setAdapter(pontuacaoAdapter);

        buscarUtilizadores();

        return root;
    }

    private void buscarUtilizadores() {
        DatabaseReference contasRef = DefinicaoFirebase.recuperarBaseDados().child("contas");
        AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Pontuações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        contasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialog.show();
                listaContas.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Conta conta = dados.getValue(Conta.class);
                    if (conta.getTipo().equals(Configs.grupos[0]))
                        listaContas.add(conta);
                }

                Collections.sort(listaContas, (o1, o2) -> Integer.compare(o1.getTotalXP(), o2.getTotalXP()));
                Collections.reverse(listaContas);

                dialog.dismiss();
                pontuacaoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}