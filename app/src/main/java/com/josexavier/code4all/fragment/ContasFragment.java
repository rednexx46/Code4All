package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.josexavier.code4all.adapter.ContasAdminAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Conta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ContasFragment extends Fragment {

    private List<Conta> listaContas = new ArrayList<>();
    private List<Conta> listaFiltrada = new ArrayList<>();
    private List<String> tipos = new ArrayList<>();
    private ContasAdminAdapter adapter;
    private ArrayAdapter<List<String>> spinnerAdapter;
    private Spinner spinnerTiposConta;
    private RecyclerView recyclerContas;
    private AlertDialog dialogCarregamento, dialogModificacao;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_contas, container, false);

        dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogModificacao = new SpotsDialog.Builder().setContext(getContext()).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        // Definições do RecyclerContas
        recyclerContas = root.findViewById(R.id.recyclerViewContas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerContas.getContext(), RecyclerView.VERTICAL);
        adapter = new ContasAdminAdapter(listaContas, getContext());

        // Configurar RecyclerView
        recyclerContas.setLayoutManager(layoutManager);
        recyclerContas.setItemAnimator(new DefaultItemAnimator());
        recyclerContas.setHasFixedSize(true);
        recyclerContas.addItemDecoration(dividerItemDecoration);
        recyclerContas.setAdapter(adapter);
        recyclerContas.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerContas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listaFiltrada.size() == 0)
                    cliqueCurto(position, listaContas);
                else
                    cliqueCurto(position, listaFiltrada);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarContas);
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

        buscarContas();

        return root;

    }

    private void cliqueCurto(int position, List<Conta> listaContas) {

        // Colocar Spinner
        // Fazer array com 3 tipos de conta (membro, moderador e admin)
        // Se for igual ao tipo atual do utilizador, salta essa posicao

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.privilegios_conta_layout, null);
        builder.setView(dialogView);

        spinnerTiposConta = dialogView.findViewById(R.id.spinnerTiposConta);
        preencherTipos(listaContas.get(position).getTipo()); // Preencher tipos consoante o tipo de utilizador logado (mod/admin)

        builder.setTitle("Editar Privilégios do Utilizador");
        builder.setMessage("Você está atualmente a editar o Utilizador \"" + listaContas.get(position).getNome() + "\"");
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            dialogModificacao.show();
            HashMap<String, Object> tipoConta = new HashMap<>();

            int posicao = spinnerTiposConta.getSelectedItemPosition();

            tipoConta.put("tipo", tipos.get(posicao));

            DatabaseReference contaSelecionadaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(listaContas.get(position).getId());

            contaSelecionadaRef.updateChildren(tipoConta).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Tipo de Conta atualizado com Sucesso!", Toast.LENGTH_SHORT).show();
                    dialogModificacao.dismiss();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    dialogModificacao.dismiss();
                    dialog.dismiss();
                }
            });

            dialog.dismiss();

        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        spinnerTiposConta.setSelection(0);
        builder.show();
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        for (Conta conta : listaContas) {
            if (conta.getNome().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(conta);
            }
        }
        adapter.filtrarDados(listaFiltrada);
    }

    private void preencherTipos(String tipoUtilizadorSelecionado) {
        tipos.clear();
        Configs.recuperarGrupo(grupo -> {

            switch (grupo) {

                case "mod":
                    tipos.add(tipoUtilizadorSelecionado);
                    if (!tipoUtilizadorSelecionado.equals(Configs.grupos[0]))
                        tipos.add(Configs.grupos[0]);
                    if (!tipoUtilizadorSelecionado.equals(Configs.grupos[1]))
                        tipos.add(Configs.grupos[1]);
                    break;
                case "admin":
                    tipos.add(tipoUtilizadorSelecionado);
                    if (!tipoUtilizadorSelecionado.equals(Configs.grupos[0]))
                        tipos.add(Configs.grupos[0]);
                    if (!tipoUtilizadorSelecionado.equals(Configs.grupos[1]))
                        tipos.add(Configs.grupos[1]);
                    if (!tipoUtilizadorSelecionado.equals(Configs.grupos[2]))
                        tipos.add(Configs.grupos[2]);
                    break;

            }
            spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, tipos);
            spinnerTiposConta.setAdapter(spinnerAdapter);
        });

    }

    private void buscarContas() {
        DatabaseReference contasRef = DefinicaoFirebase.recuperarBaseDados().child("contas");

        contasRef.orderByChild("tipo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialogCarregamento.show();
                listaContas.clear();
                String tipo = snapshot.child(Configs.recuperarIdUtilizador()).child("tipo").getValue(String.class);
                for (DataSnapshot dados : snapshot.getChildren()) {

                    String idUtilizador = dados.getValue(Conta.class).getId();
                    String tipoUtilizador = dados.getValue(Conta.class).getTipo();

                    // Se o utilizador for diferente daquele que se encontra já logado
                    // Então adiciona para a listaContas
                    if (dados.getValue(Conta.class).getTipo() != null && !dados.getValue(Conta.class).getTipo().equals("")) {

                        if (tipo.equals("mod")) {
                            if (!idUtilizador.equals(Configs.recuperarIdUtilizador())) {
                                if (!tipoUtilizador.equals("admin") && !tipoUtilizador.equals("empresa"))
                                    listaContas.add(dados.getValue(Conta.class));
                            }
                        } else if (tipo.equals("admin")) {
                            if (!idUtilizador.equals(Configs.recuperarIdUtilizador()))
                                if (!tipoUtilizador.equals("empresa"))
                                    listaContas.add(dados.getValue(Conta.class));
                        }

                    }
                }

                dialogCarregamento.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

}