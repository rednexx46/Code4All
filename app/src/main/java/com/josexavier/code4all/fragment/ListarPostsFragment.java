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
import com.josexavier.code4all.activity.EditarPostActivity;
import com.josexavier.code4all.adapter.PostAdminAdapter;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Post;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListarPostsFragment extends Fragment {

    private List<Post> listaFiltrada = new ArrayList<>();
    private List<Post> listaPosts = new ArrayList<>();
    private PostAdminAdapter postAdminAdapter;
    private AlertDialog dialogCarregamento, dialogPostRemocao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_listar_post, container, false);

        dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Posts...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogPostRemocao = new SpotsDialog.Builder().setContext(getContext()).setMessage("Removendo Post...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        EditText editTextPesquisar;
        editTextPesquisar = root.findViewById(R.id.editTextPesquisarPost);
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

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewPosts);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), RecyclerView.VERTICAL);
        postAdminAdapter = new PostAdminAdapter(listaPosts, getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(postAdminAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) { // Editar Post
                if (listaFiltrada.size() == 0)
                    cliqueCurto(position, listaPosts);
                else
                    cliqueCurto(position, listaFiltrada);
            }

            @Override
            public void onLongItemClick(View view, int position) { // Remover Post
                if (listaFiltrada.size() == 0)
                    cliqueLongo(position, listaPosts);
                else
                    cliqueLongo(position, listaFiltrada);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        buscarPosts(DefinicaoFirebase.recuperarBaseDados().child("posts"));

        return root;

    }

    private void cliqueLongo(int position, List<Post> listaPosts) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle("Remoção do Post");
        dialog.setMessage("Tem a certeza que pretende eliminar o Post " + listaPosts.get(position).getTitulo() + "?");

        dialog.setPositiveButton("Sim", (dialog2, which) -> {
            dialogPostRemocao.show();

            StorageReference fotoRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("posts").child(listaPosts.get(position).getId() + ".png");
            fotoRef.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DefinicaoFirebase.recuperarBaseDados().child("posts").child(listaPosts.get(position).getId()).removeValue((error, ref) -> {
                        if (error == null) {
                            dialogPostRemocao.dismiss();
                            dialog2.dismiss();
                            Toast.makeText(getContext(), "Post removido com Sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogPostRemocao.dismiss();
                            dialog2.dismiss();
                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialogPostRemocao.dismiss();
                    dialog2.dismiss();
                    Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                }
            });

            postAdminAdapter.notifyItemRemoved(position);
        });

        dialog.setNegativeButton("Não", (dialog2, which) -> dialog2.dismiss());

        dialog.show();
    }

    private void cliqueCurto(int position, List<Post> listaPosts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Post");
        builder.setMessage("Está atualmente a editar o Post \"" + listaPosts.get(position).getTitulo() + "\"");
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            Intent editarPost = new Intent(getContext(), EditarPostActivity.class);
            editarPost.putExtra("idPost", listaPosts.get(position).getId());
            startActivity(editarPost);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        for (Post post : listaPosts) {
            if (post.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(post);
            }
        }
        postAdminAdapter.filtrarDados(listaFiltrada);
    }

    private void buscarPosts(DatabaseReference ref) {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogCarregamento.show();
                listaPosts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    listaPosts.add(post);
                }
                dialogCarregamento.dismiss();
                postAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}