package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.adapter.PostsAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Post;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PostsActivity extends AppCompatActivity {

    private RecyclerView recyclerPosts;
    private List<Post> listaPosts = new ArrayList<>();
    private PostsAdapter adapter;
    private AlertDialog dialog;
    private DatabaseReference meusPostsRef;
    private ValueEventListener meusPostsEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        AlertDialog dialogRemocao = new SpotsDialog.Builder().setContext(this).setMessage("Removendo Post...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        // Inicialização do RecyclerView e dos seus elementos
        recyclerPosts = findViewById(R.id.recyclerViewMinhasPostagens);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerPosts.getContext(), RecyclerView.VERTICAL);
        adapter = new PostsAdapter(listaPosts, getApplicationContext());

        // Configurar RecyclerView
        recyclerPosts.setLayoutManager(layoutManager);
        recyclerPosts.setItemAnimator(new DefaultItemAnimator());
        recyclerPosts.setHasFixedSize(true);
        recyclerPosts.addItemDecoration(dividerItemDecoration);
        recyclerPosts.setAdapter(adapter);

        recyclerPosts.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPosts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) { // Editar Post
                if (listaPosts.get(position).getEstado().equals(Configs.PENDENTE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostsActivity.this);
                    builder.setTitle("Editar Post");
                    builder.setMessage("Está atualmente a editar o Post \"" + listaPosts.get(position).getTitulo() + "\"");
                    builder.setPositiveButton("Confirmar", (dialog, which) -> {
                        Intent editarPost = new Intent(getApplicationContext(), EditarPostActivity.class);
                        editarPost.putExtra("idPost", listaPosts.get(position).getId());
                        startActivity(editarPost);
                    });
                    builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) { // Remover Post
                AlertDialog.Builder dialog = new AlertDialog.Builder(PostsActivity.this);

                dialog.setTitle("Remoção do Post");
                dialog.setMessage("Tem a certeza que pretende eliminar o Post " + listaPosts.get(position).getTitulo() + "?");

                dialog.setPositiveButton("Sim", (dialog2, which) -> {
                    try {
                        dialogRemocao.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DefinicaoFirebase.recuperarBaseDados().child("posts").child(listaPosts.get(position).getId()).removeValue((error, ref) -> {
                        if (error == null) {
                            dialogRemocao.dismiss();
                            Toast.makeText(getApplicationContext(), "Post removido com Sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogRemocao.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });
                    adapter.notifyItemRemoved(position);
                });

                dialog.setNegativeButton("Não", (dialog2, which) -> dialog2.dismiss());

                dialog.show();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        FloatingActionButton fabCriarPost;
        fabCriarPost = findViewById(R.id.fabAdicionarPost);
        fabCriarPost.setOnClickListener(v -> {
            Intent criarPostActivity = new Intent(getApplicationContext(), CriarPostActivity.class);
            criarPostActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(criarPostActivity);
        });

        buscarMeusPosts();

    }

    private void buscarMeusPosts() {
        meusPostsRef = DefinicaoFirebase.recuperarBaseDados().child("posts");
        meusPostsEventListener = meusPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Limpar Posts (evitar posts duplicados)
                listaPosts.clear();

                String idUtilizador = Configs.recuperarIdUtilizador();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (dados.getValue(Post.class).getIdCriador().equals(idUtilizador)) { // Adicionar para a lista apenas aqueles que são do utilizador logado.
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
    protected void onDestroy() {
        super.onDestroy();
        meusPostsRef.removeEventListener(meusPostsEventListener);
    }
}