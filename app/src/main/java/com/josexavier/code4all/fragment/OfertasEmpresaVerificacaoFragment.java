package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.josexavier.code4all.adapter.OfertasEmpresaVerificacaoAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Oferta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class OfertasEmpresaVerificacaoFragment extends Fragment {

    private final List<Oferta> listaOfertas = new ArrayList<>();
    private OfertasEmpresaVerificacaoAdapter ofertasEmpresaVerificacaoAdapter;
    private DatabaseReference ofertasRef;
    private ValueEventListener ofertasEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_ofertas_empresa_verificacao, container, false);

        // Configuração do RecyclerView
        RecyclerView recyclerViewOfertas = root.findViewById(R.id.recyclerViewOfertasEmpresa);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewOfertas.getContext(), RecyclerView.VERTICAL);
        recyclerViewOfertas.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerViewOfertas.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOfertas.addItemDecoration(dividerItemDecoration);
        recyclerViewOfertas.setHasFixedSize(true);

        // Configuracao Adapter
        ofertasEmpresaVerificacaoAdapter = new OfertasEmpresaVerificacaoAdapter(listaOfertas, getContext());
        recyclerViewOfertas.setAdapter(ofertasEmpresaVerificacaoAdapter);

        buscarOfertas();

        return root;

    }

    private void buscarOfertas() { // vai buscar as ofertas e mostra uma janela de carregamento
        ofertasRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas");
        AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        ofertasEventListener = ofertasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listaOfertas.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (Objects.requireNonNull(dados.getValue(Oferta.class)).getEstado().equals(Configs.ACEITE))
                        listaOfertas.add(dados.getValue(Oferta.class));
                }

                dialog.dismiss();

                ofertasEmpresaVerificacaoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ofertasRef.removeEventListener(ofertasEventListener);
    }
}