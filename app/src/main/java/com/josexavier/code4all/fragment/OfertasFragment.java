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

import com.josexavier.code4all.R;
import com.josexavier.code4all.adapter.OfertasAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Oferta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OfertasFragment extends Fragment {

    private List<Oferta> listaOfertas = new ArrayList<>();
    private OfertasAdapter ofertasAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_ofertas, container, false);

        // Configurações RecyclerView
        RecyclerView recyclerViewOfertas = root.findViewById(R.id.recyclerViewOfertas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewOfertas.getContext(), RecyclerView.VERTICAL);
        recyclerViewOfertas.setHasFixedSize(true);
        recyclerViewOfertas.setLayoutManager(layoutManager);
        recyclerViewOfertas.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOfertas.addItemDecoration(dividerItemDecoration);

        // Configurações Adapter
        ofertasAdapter = new OfertasAdapter(listaOfertas, getContext());
        recyclerViewOfertas.setAdapter(ofertasAdapter);

        buscarOfertas();

        return root;
    }

    private void buscarOfertas() { // Buscar ofertas do utilizador
        Configs.recuperarIdUtilizador(idUtilizador -> {
            DatabaseReference referenciaOfertas = DefinicaoFirebase.recuperarBaseDados().child("ofertas");
            AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Ofertas...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

            referenciaOfertas.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dialog.show();
                    listaOfertas.clear();
                    for (DataSnapshot dados : snapshot.getChildren()) {
                        Oferta oferta = dados.getValue(Oferta.class);
                        if (oferta.getIdUtilizador().equals(idUtilizador) && oferta.getEstado().equals(Configs.PENDENTE))
                            listaOfertas.add(dados.getValue(Oferta.class));
                    }
                    dialog.dismiss();
                    ofertasAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


    }


}