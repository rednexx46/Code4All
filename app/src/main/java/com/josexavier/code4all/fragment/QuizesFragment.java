package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.adapter.QuizesPopularesAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.interfaces.Validacao;
import com.josexavier.code4all.model.Quiz;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class QuizesFragment extends Fragment {

    private List<Quiz> listaQuizes = new ArrayList<>();
    private List<Quiz> listaInscricoes = new ArrayList<>();
    private QuizesPopularesAdapter quizesPopularesAdapter, inscricoesAdapter;
    private RecyclerView recyclerViewQuizesPopulares, recyclerViewInscricoes;
    private TextView textViewSubscricoes;
    private AlertDialog dialog;
    private DatabaseReference quizSubscritoRef, quizesRef;
    private ValueEventListener quizSubscritosEventListener, quizesEventListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_quizes, container, false);

        textViewSubscricoes = root.findViewById(R.id.textViewSloganSubscricoesQuizes);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Quizes...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        // Definição do RecyclerViewQuizesPopulares
        recyclerViewQuizesPopulares = root.findViewById(R.id.recyclerViewQuizesPopulares);
        recyclerViewQuizesPopulares.setLayoutManager(criarLayoutManager());
        recyclerViewQuizesPopulares.setItemAnimator(new DefaultItemAnimator());
        recyclerViewQuizesPopulares.setHasFixedSize(true);

        //Configuração do Adapter (RecyclerViewQuizesPopulares)
        quizesPopularesAdapter = new QuizesPopularesAdapter(listaQuizes, getContext());
        recyclerViewQuizesPopulares.setAdapter(quizesPopularesAdapter);

        // Definição do recyclerViewInscricoes
        recyclerViewInscricoes = root.findViewById(R.id.recyclerViewInscricoesQuizes);
        recyclerViewInscricoes.setLayoutManager(criarLayoutManager());
        recyclerViewInscricoes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewInscricoes.setHasFixedSize(true);

        // Configuração do Adapter (recyclerViewInscricoes)
        inscricoesAdapter = new QuizesPopularesAdapter(listaInscricoes, getContext());
        recyclerViewInscricoes.setAdapter(inscricoesAdapter);


        buscarQuizes(sucesso -> {
            if (sucesso)
                Configs.recuperarIdUtilizador(idUtilizador -> buscarInscricoes(idUtilizador));
            else
                Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
        });


        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quizSubscritoRef.removeEventListener(quizSubscritosEventListener);
        quizesRef.removeEventListener(quizesEventListener);
    }

    private LinearLayoutManager criarLayoutManager() {
        return new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
    }

    private void buscarInscricoes(String idUtilizador) {
        DatabaseReference inscricoesRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador).child("temas");
        inscricoesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaInscricoes.clear();
                textViewSubscricoes.setText("Subscrições Atuais .: ");
                for (DataSnapshot dados : snapshot.getChildren()) {

                    String tema = dados.getValue(String.class);

                    textViewSubscricoes.setText(textViewSubscricoes.getText() + "" + tema + " | ");

                    buscarQuizesSubscricao(tema, validar -> {
                        if (validar) {
                            dialog.dismiss();
                            inscricoesAdapter.notifyDataSetChanged();

                        } else
                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });
    }

    private void buscarQuizesSubscricao(String tema, Validacao sucesso) {
        quizSubscritoRef = DefinicaoFirebase.recuperarBaseDados().child("quizes");
        quizSubscritosEventListener = quizSubscritoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Quiz quiz = dados.getValue(Quiz.class);
                    if (quiz.getTema().equals(tema))
                        listaInscricoes.add(quiz);
                }

                Collections.sort(listaInscricoes, (o1, o2) -> Double.compare(o1.getClassificacao(), o2.getClassificacao()));
                Collections.reverse(listaInscricoes);

                sucesso.isValidacaoSucesso(true);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void buscarQuizes(Validacao validacao) {

        dialog.show();

        quizesRef = DefinicaoFirebase.recuperarBaseDados().child("quizes");

        quizesEventListener = quizesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaQuizes.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Quiz quiz = postSnapshot.getValue(Quiz.class);
                    listaQuizes.add(quiz);
                }

                Collections.sort(listaQuizes, (o1, o2) -> Double.compare(o1.getClassificacao(), o2.getClassificacao()));
                Collections.reverse(listaQuizes);

                quizesPopularesAdapter.notifyDataSetChanged();
                validacao.isValidacaoSucesso(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}