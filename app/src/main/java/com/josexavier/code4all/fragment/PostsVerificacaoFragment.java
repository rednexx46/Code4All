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
import com.josexavier.code4all.adapter.PostsVerificacaoAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Post;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class PostsVerificacaoFragment extends Fragment {

    private List<Post> listaPosts = new ArrayList<>();
    private PostsVerificacaoAdapter adapter;
    private AlertDialog dialog;
    private DatabaseReference postsRef;
    private ValueEventListener postsEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_posts_verificacao, container, false);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Posts...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        // Inicialização do RecyclerView e dos seus elementos
        RecyclerView recyclerPostsVerificacao;
        recyclerPostsVerificacao = root.findViewById(R.id.recyclerViewPostsVerificacao);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerPostsVerificacao.getContext(), RecyclerView.VERTICAL);
        adapter = new PostsVerificacaoAdapter(listaPosts, requireActivity().getApplicationContext());

        // Configurar RecyclerView
        recyclerPostsVerificacao.setLayoutManager(layoutManager);
        recyclerPostsVerificacao.setItemAnimator(new DefaultItemAnimator());
        recyclerPostsVerificacao.setHasFixedSize(true);
        recyclerPostsVerificacao.addItemDecoration(dividerItemDecoration);
        recyclerPostsVerificacao.setAdapter(adapter);

        buscarPosts();

        return root;

    }

    private void buscarPosts() {
        postsRef = DefinicaoFirebase.recuperarBaseDados().child("posts");
        postsEventListener = postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listaPosts.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (Objects.requireNonNull(dados.getValue(Post.class)).getEstado().equals(Configs.PENDENTE)) {
                        listaPosts.add(dados.getValue(Post.class));
                    }
                }
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
        postsRef.removeEventListener(postsEventListener);
    }
}