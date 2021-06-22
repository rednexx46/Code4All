package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.storage.StorageReference;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.EditarNotificacaoActivity;
import com.josexavier.code4all.adapter.NotificacoesAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Notificacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListarNotificacoesFragment extends Fragment {

    private List<Notificacao> listaNotificacoes = new ArrayList<>();
    private List<Notificacao> listaFiltrada = new ArrayList<>();
    private NotificacoesAdapter notificacoesAdapter;
    private AlertDialog dialogRemocao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_listar_notificacoes, container, false);

        dialogRemocao = new SpotsDialog.Builder().setContext(getContext()).setMessage("Removendo Notificação...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        RecyclerView recyclerViewNotificacoes;
        recyclerViewNotificacoes = root.findViewById(R.id.recyclerViewListarNotificacoes);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewNotificacoes.getContext(), RecyclerView.VERTICAL);

        recyclerViewNotificacoes.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerViewNotificacoes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewNotificacoes.addItemDecoration(dividerItemDecoration);
        recyclerViewNotificacoes.setHasFixedSize(true);

        // Configuracao Adapter
        notificacoesAdapter = new NotificacoesAdapter(listaNotificacoes, getContext());
        recyclerViewNotificacoes.setAdapter(notificacoesAdapter);

        recyclerViewNotificacoes.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerViewNotificacoes, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listaFiltrada.size() == 0)
                    cliqueCurto(position, listaNotificacoes);
                else
                    cliqueCurto(position, listaFiltrada);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                if (listaFiltrada.size() == 0)
                    cliqueLongo(position, listaNotificacoes);
                else
                    cliqueLongo(position, listaFiltrada);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarNotificacoes);
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

        buscarNotificacoes();

        return root;

    }

    private void cliqueCurto(int position, List<Notificacao> listaNotificacoes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Notificação");
        builder.setMessage("Está atualmente a editar o Notificação \"" + listaNotificacoes.get(position).getTitulo() + "\"");
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            Intent editarPost = new Intent(getContext(), EditarNotificacaoActivity.class);
            editarPost.putExtra("idNotificacao", listaNotificacoes.get(position).getId());
            startActivity(editarPost);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        for (Notificacao notificacao : listaNotificacoes) {
            if (notificacao.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(notificacao);
            }
        }
        notificacoesAdapter.filtrarDados(listaFiltrada);
    }

    private void cliqueLongo(int position, List<Notificacao> listaNotificacoes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Remoção da Notificação");
        builder.setMessage("Tem a certeza que pretende eliminar a Notificação " + listaNotificacoes.get(position).getTitulo() + "?");

        builder.setPositiveButton("Sim", (dialog, which) -> {
            dialogRemocao.show();
            StorageReference fotoRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("notificacoes").child(listaNotificacoes.get(position).getId() + ".png");
            fotoRef.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DefinicaoFirebase.recuperarBaseDados().child("notificacoes").child(listaNotificacoes.get(position).getId()).removeValue((error, ref) -> {
                        if (error == null) {
                            dialogRemocao.dismiss();
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Post removido com Sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogRemocao.dismiss();
                            dialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialogRemocao.dismiss();
                    dialog.dismiss();
                    Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                }
            });

            notificacoesAdapter.notifyItemRemoved(position);
        });

        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        builder.show();
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