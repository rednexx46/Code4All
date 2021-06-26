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
import com.josexavier.code4all.adapter.EmpresasAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Empresa;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class EmpresasFragment extends Fragment {

    private List<Empresa> listaEmpresas = new ArrayList<>();
    private EmpresasAdapter empresasAdapter;
    private AlertDialog dialog;
    private DatabaseReference empresasRef;
    private ValueEventListener empresasEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_empresas, container, false);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Empresas...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarEmpresa);
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

        // Configuração do Recycler View
        RecyclerView recyclerViewEmpresas;
        recyclerViewEmpresas = root.findViewById(R.id.recyclerViewEmpresas);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewEmpresas.getContext(), RecyclerView.VERTICAL);
        recyclerViewEmpresas.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerViewEmpresas.setItemAnimator(new DefaultItemAnimator());
        recyclerViewEmpresas.addItemDecoration(dividerItemDecoration);
        recyclerViewEmpresas.setHasFixedSize(true);

        // Configuração do Adapter
        empresasAdapter = new EmpresasAdapter(listaEmpresas, getContext());
        recyclerViewEmpresas.setAdapter(empresasAdapter);

        Configs.recuperarIdUtilizador(idUtilizador -> buscarEmpresas(idUtilizador));


        return root;

    }


    private void filtrar(String text) {
        List<Empresa> listaFiltrada = new ArrayList<>();
        for (Empresa empresa : listaEmpresas) {
            if (empresa.getNome().toLowerCase().contains(text.toLowerCase())) {
                listaFiltrada.add(empresa);
            }
        }
        empresasAdapter.filtrarDados(listaFiltrada);
    }

    private void buscarEmpresas(String idUtilizador) {
        empresasRef = DefinicaoFirebase.recuperarBaseDados().child("contas");
        empresasEventListener = empresasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialog.show();
                listaEmpresas.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Empresa empresa = dados.getValue(Empresa.class);

                    if (empresa.getTipo().equals(Configs.grupos[3]) && !empresa.getId().equals(idUtilizador))
                        listaEmpresas.add(empresa);

                }
                empresasAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        empresasRef.removeEventListener(empresasEventListener);
    }
}