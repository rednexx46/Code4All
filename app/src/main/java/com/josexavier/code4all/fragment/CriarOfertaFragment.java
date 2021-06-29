package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.josexavier.code4all.adapter.ContasEmpresaAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Conta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CriarOfertaFragment extends Fragment {

    private List<Conta> listaContas = new ArrayList<>();
    private List<Conta> listaFiltrada = new ArrayList<>();
    private ContasEmpresaAdapter adapter;

    private DatabaseReference contasRef;
    private ValueEventListener contasEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_criar_oferta, container, false);

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarContasCriarOferta);
        editTextPesquisar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });

        // Configuração do RecyclerView
        RecyclerView recyclerViewOfertas;
        recyclerViewOfertas = root.findViewById(R.id.recyclerViewContasCriarOferta);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewOfertas.getContext(), RecyclerView.VERTICAL);
        recyclerViewOfertas.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerViewOfertas.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOfertas.addItemDecoration(dividerItemDecoration);
        recyclerViewOfertas.setHasFixedSize(true);

        // Configuração do Adapter
        adapter = new ContasEmpresaAdapter(listaContas, getContext());
        recyclerViewOfertas.setAdapter(adapter);

        buscarContas();

        return root;

    }

    private void filtrar(String text) {
        listaFiltrada.clear();
        for (Conta conta : listaContas) {
            if (conta.getNome().toLowerCase().contains(text.toLowerCase())) {
                listaFiltrada.add(conta);
            }
        }
        adapter.filtrarDados(listaFiltrada);
    }

    private void buscarContas() { // buscar todas as contas que sejam do tipo "membro"

        contasRef = DefinicaoFirebase.recuperarBaseDados().child("contas");
        contasEventListener = contasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaContas.clear();

                AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Ofertas...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (DataSnapshot dados : snapshot.getChildren()) {
                    String tipo = dados.getValue(Conta.class).getTipo();

                    if (tipo.equals("membro"))
                        listaContas.add(dados.getValue(Conta.class));
                }

                Collections.sort(listaContas, (o1, o2) -> Integer.compare(o1.getTotalXP(), o2.getTotalXP()));
                Collections.reverse(listaContas);

                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contasRef.removeEventListener(contasEventListener);
    }
}