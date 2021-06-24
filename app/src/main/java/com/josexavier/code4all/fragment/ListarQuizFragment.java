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
import com.josexavier.code4all.activity.EditarQuizActivity;
import com.josexavier.code4all.adapter.QuizesAdminAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Quiz;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListarQuizFragment extends Fragment {

    private final List<Quiz> listaQuizes = new ArrayList<>();
    private List<Quiz> listaFiltrada = new ArrayList<>();
    private QuizesAdminAdapter quizesAdapter;
    private AlertDialog dialogRemocao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_listar_quiz, container, false);

        dialogRemocao = new SpotsDialog.Builder().setContext(getContext()).setMessage("Removendo Quiz...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewQuizesAdmin);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), RecyclerView.VERTICAL);
        quizesAdapter = new QuizesAdminAdapter(listaQuizes, getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(quizesAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) { // Editar Quiz
                if (listaFiltrada.size() == 0) {
                    cliqueCurto(position, listaQuizes);
                } else {
                    cliqueCurto(position, listaFiltrada);
                }

            }

            @Override
            public void onLongItemClick(View view, int position) { // Remover Quiz
                if (listaFiltrada.size() == 0) {
                    cliqueLongo(position, listaQuizes);
                } else {
                    cliqueLongo(position, listaFiltrada);
                }
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarQuiz);
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

        buscarQuizes(DefinicaoFirebase.recuperarBaseDados().child("quizes"));

        return root;

    }

    private void cliqueLongo(int position, List<Quiz> listaQuizes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Remoção do Quiz");
        builder.setMessage("Tem a certeza que pretende eliminar o quiz " + listaQuizes.get(position).getTitulo() + "?");

        builder.setPositiveButton("Sim", (dialog, which) -> {
            dialogRemocao.show();
            StorageReference fotoRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("quizes").child(listaQuizes.get(position).getId() + ".png");
            fotoRef.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DefinicaoFirebase.recuperarBaseDados().child("quizes").child(listaQuizes.get(position).getId()).removeValue((error, ref) -> {
                        if (error == null) {
                            dialogRemocao.dismiss();
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Quiz removido com Sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogRemocao.dismiss();
                            dialog.dismiss();
                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialogRemocao.dismiss();
                    Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                }
            });

            quizesAdapter.notifyItemRemoved(position);
        });

        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void cliqueCurto(int position, List<Quiz> listaQuizes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar informações do Quiz");
        builder.setMessage("Está atualmente a editar o Quiz \"" + listaQuizes.get(position).getTitulo() + "\"");
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            Intent editarPost = new Intent(getContext(), EditarQuizActivity.class);
            editarPost.putExtra("idQuiz", listaQuizes.get(position).getId());
            startActivity(editarPost);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        for (Quiz quiz : listaQuizes) {
            if (quiz.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(quiz);
            }
        }
        quizesAdapter.filtrarDados(listaFiltrada);
    }

    private void buscarQuizes(DatabaseReference referenciaQuizes) {

        referenciaQuizes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaQuizes.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Quiz quiz = postSnapshot.getValue(Quiz.class);
                    listaQuizes.add(quiz);
                }

                quizesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}