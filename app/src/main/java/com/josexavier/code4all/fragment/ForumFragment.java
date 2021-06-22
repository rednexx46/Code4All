package com.josexavier.code4all.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.PostActivity;
import com.josexavier.code4all.activity.PostsActivity;
import com.josexavier.code4all.adapter.PostsAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Post;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {

    private List<Post> listaPost = new ArrayList<>();
    private List<Post> listaFiltrada = new ArrayList<>();
    private RecyclerView recyclerForum;
    private PostsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forum, container, false);

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarPostForum);
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

        recyclerForum = root.findViewById(R.id.recyclerViewForum);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerForum.setLayoutManager(layoutManager);
        recyclerForum.setItemAnimator(new DefaultItemAnimator());
        recyclerForum.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerForum.getContext(), RecyclerView.VERTICAL);
        recyclerForum.addItemDecoration(dividerItemDecoration);
        adapter = new PostsAdapter(listaPost, getActivity().getApplicationContext());
        recyclerForum.setAdapter(adapter);

        recyclerForum.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerForum, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listaFiltrada.size() == 0)
                    cliqueCurto(position, listaPost);
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

        FloatingActionButton fabPerguntas;
        fabPerguntas = root.findViewById(R.id.fabForumPerguntas);
        Configs.recuperarGrupo(grupo -> {
            if (grupo.equals(Configs.grupos[0])) {
                fabPerguntas.setVisibility(View.VISIBLE);
                fabPerguntas.setOnClickListener(v -> {
                    Intent postsActivity = new Intent(getContext(), PostsActivity.class);
                    postsActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(postsActivity);
                });
            } else {
                fabPerguntas.setVisibility(View.GONE);
            }
        });

        buscarForum();

        return root;
    }

    private void cliqueCurto(int position, List<Post> listaPost) {
        Intent intentPostActivity = new Intent(getContext(), PostActivity.class);
        intentPostActivity.putExtra("idPost", listaPost.get(position).getId());
        intentPostActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentPostActivity);
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        for (Post post : listaPost) {
            if (post.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(post);
            }
        }
        adapter.filtrarDados(listaFiltrada);
    }

    private void buscarForum() {

        DatabaseReference forum = DefinicaoFirebase.recuperarBaseDados().child("posts");

        Query queryForum = forum.orderByChild("estado").equalTo("aceite");

        queryForum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPost.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Post post = dados.getValue(Post.class);
                    listaPost.add(post);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}