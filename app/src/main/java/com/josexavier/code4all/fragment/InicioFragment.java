package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.josexavier.code4all.adapter.QuizesSubscritosAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Quiz;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class InicioFragment extends Fragment {

    private ValueEventListener eventoQuizes;
    private DatabaseReference quizes;
    private List<Quiz> listaQuizes = new ArrayList<>(), listaQuizesSubscritos = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inicio, container, false);

        RecyclerView recyclerQuizesSubscritos = root.findViewById(R.id.recyclerViewQuizesSubscritos);
        recyclerQuizesSubscritos.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.HORIZONTAL, false));
        recyclerQuizesSubscritos.setItemAnimator(new DefaultItemAnimator());
        recyclerQuizesSubscritos.setHasFixedSize(true);

        // Separação //

        RecyclerView recyclerQuizesFinalizados = root.findViewById(R.id.recyclerViewQuizesFinalizados);
        recyclerQuizesFinalizados.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.HORIZONTAL, false));
        recyclerQuizesFinalizados.setItemAnimator(new DefaultItemAnimator());
        recyclerQuizesFinalizados.setHasFixedSize(true);

        try {
            buscarQuizes(recyclerQuizesSubscritos, recyclerQuizesFinalizados);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return root;
    }

    private void buscarQuizes(RecyclerView recyclerViewSubscritos, RecyclerView recyclerViewFinalizados) {
        AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Quizes...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        String idUtilizador = DefinicaoFirebase.recuperarAutenticacao().getUid();
        quizes = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador).child("inscricoes");

        eventoQuizes = quizes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listaQuizes.clear();
                listaQuizesSubscritos.clear();

                for (DataSnapshot dados : snapshot.getChildren()) {
                    Quiz quizSubscrito = dados.getValue(Quiz.class);
                    if (quizSubscrito.getProgresso() == 100)
                        listaQuizesSubscritos.add(quizSubscrito);
                    else
                        listaQuizes.add(quizSubscrito);

                }

                //Configuração do Adapter (RecyclerViewSubscritos)
                QuizesSubscritosAdapter quizesAdapterSubscritos = new QuizesSubscritosAdapter(listaQuizes, getContext());
                recyclerViewSubscritos.setAdapter(quizesAdapterSubscritos);
                quizesAdapterSubscritos.notifyDataSetChanged();

                //Configuração do Adapter (RecyclerViewFinalizados)
                QuizesSubscritosAdapter quizesAdapterFinalizados = new QuizesSubscritosAdapter(listaQuizesSubscritos, getActivity().getApplicationContext());
                recyclerViewFinalizados.setAdapter(quizesAdapterFinalizados);
                quizesAdapterFinalizados.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            quizes.removeEventListener(eventoQuizes);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}