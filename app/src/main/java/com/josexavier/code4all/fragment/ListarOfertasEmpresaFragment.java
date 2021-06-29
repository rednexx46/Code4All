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
import com.josexavier.code4all.adapter.OfertasEmpresaAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Oferta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListarOfertasEmpresaFragment extends Fragment {

    private List<Oferta> listaOfertas = new ArrayList<>();
    private OfertasEmpresaAdapter ofertasEmpresaAdapter;

    private DatabaseReference ofertasRef;
    private ValueEventListener ofertasEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar_ofertas_empresa, container, false);

        // Configuração RecyclerView
        RecyclerView recyclerViewOfertas;
        recyclerViewOfertas = root.findViewById(R.id.recyclerViewListarOfertas);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewOfertas.getContext(), RecyclerView.VERTICAL);
        recyclerViewOfertas.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerViewOfertas.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOfertas.addItemDecoration(dividerItemDecoration);
        recyclerViewOfertas.setHasFixedSize(true);

        // Configuração Adapter
        ofertasEmpresaAdapter = new OfertasEmpresaAdapter(listaOfertas, getContext());
        recyclerViewOfertas.setAdapter(ofertasEmpresaAdapter);

        buscarOfertas(Configs.recuperarIdUtilizador());

        return root;

    }

    private void buscarOfertas(String idEmpresa) { // Buscar todas as ofertas da empresa para listar as mesmas
        AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Ofertas...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        ofertasRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas");
        ofertasEventListener = ofertasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaOfertas.clear();
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (dados.getValue(Oferta.class).getIdEmpresa().equals(idEmpresa))
                        listaOfertas.add(dados.getValue(Oferta.class));
                }

                dialog.dismiss();
                ofertasEmpresaAdapter.notifyDataSetChanged();

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