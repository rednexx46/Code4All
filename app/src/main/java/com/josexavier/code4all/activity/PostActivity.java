package com.josexavier.code4all.activity;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.adapter.ComentariosAdapter;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.RecyclerItemClickListener;
import com.josexavier.code4all.model.Comentario;
import com.josexavier.code4all.model.Gosto;
import com.josexavier.code4all.model.Post;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    private ImageView imagemFoto;
    private TextView textViewTitulo, textViewTag, textViewDescricao;
    private List<Comentario> listaComentarios = new ArrayList<>();
    private ComentariosAdapter comentariosAdapter;
    private android.app.AlertDialog dialogCarregamento;
    private ValueEventListener gostosEventListener, comentariosEventListener;
    private DatabaseReference gostosRef, comentariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        String idPost = getIntent().getStringExtra("idPost");

        imagemFoto = findViewById(R.id.imageViewFotoPostVisualizacao);

        textViewTitulo = findViewById(R.id.textViewTituloPostVisualizacao);
        textViewTag = findViewById(R.id.textViewTagPostVisualizacao);
        textViewDescricao = findViewById(R.id.textViewDescricaoPostVisualizacao);

        try {
            RecyclerView recyclerViewComentarios;
            recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewComentarios.getContext(), RecyclerView.VERTICAL);

            dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Carregando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

            recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
            recyclerViewComentarios.setItemAnimator(new DefaultItemAnimator());
            recyclerViewComentarios.addItemDecoration(dividerItemDecoration);
            recyclerViewComentarios.setHasFixedSize(true);
            Configs.recuperarGrupo(grupo -> Configs.recuperarIdUtilizador(idUtilizador -> recyclerViewComentarios.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerViewComentarios, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (listaComentarios.get(position).getIdUtilizador().equals(idUtilizador) || grupo.equals(Configs.grupos[1]) || grupo.equals(Configs.grupos[2])) {
                        EditText editText = new EditText(getApplicationContext());
                        Drawable bordaPreta = ContextCompat.getDrawable(getApplicationContext(), R.drawable.borda_preta_quiz);
                        Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_medium);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.setMargins(16, 16, 16, 16);
                        editText.setBackground(bordaPreta);
                        editText.setTypeface(face);
                        editText.setLayoutParams(params);
                        editText.setHint("Introduza um comentário!");
                        editText.setText(listaComentarios.get(position).getDescricao());

                        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                        builder.setTitle("Editar Comentário");
                        builder.setMessage("Está atualmente a editar o comentário \"" + listaComentarios.get(position).getDescricao() + "\"");
                        builder.setView(editText);
                        builder.setPositiveButton("Confirmar", (dialog, which) -> {
                            String comentario = editText.getText().toString();
                            if (!comentario.isEmpty() || !comentario.equals("")) {
                                try {
                                    dialogCarregamento.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                DatabaseReference comentarioRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost).child("comentariosPost").child(listaComentarios.get(position).getId());
                                HashMap<String, Object> hashMapComentario = new HashMap<>();
                                hashMapComentario.put("descricao", comentario);
                                comentarioRef.updateChildren(hashMapComentario).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Comentário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();

                                    dialogCarregamento.dismiss();
                                    dialog.dismiss();
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Introduza algo antes de confirmar!", Toast.LENGTH_SHORT).show();
                                dialogCarregamento.dismiss();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancelar", (dialog, which) -> {
                            dialogCarregamento.dismiss();
                            dialog.dismiss();
                        });
                        builder.show();
                    }
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    if (listaComentarios.get(position).getIdUtilizador().equals(idUtilizador) || grupo.equals(Configs.grupos[1]) || grupo.equals(Configs.grupos[2])) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                        builder.setTitle("Remover Comentário");
                        builder.setCancelable(false);
                        builder.setMessage("Tem a certeza, que pretende eliminar o Comentário \"" + listaComentarios.get(position).getDescricao() + "\" ?");
                        builder.setPositiveButton("Sim", (dialog, which) -> {
                            dialogCarregamento.show();
                            DatabaseReference comentariosRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost).child("comentariosPost");
                            comentariosRef.child(listaComentarios.get(position).getId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);
                                    HashMap<String, Object> hashMapComentario = new HashMap<>();
                                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            hashMapComentario.put("comentarios", snapshot.getValue(Post.class).getComentarios() - 1);
                                            postRef.updateChildren(hashMapComentario).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Comentário Eliminado com Sucesso!", Toast.LENGTH_SHORT).show();
                                                    dialogCarregamento.dismiss();
                                                } else {
                                                    Toast.makeText(PostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                                    dialogCarregamento.dismiss();
                                                }
                                                dialog.dismiss();
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    dialogCarregamento.dismiss();
                                    dialog.dismiss();
                                }
                            });
                        });
                        builder.setNegativeButton("Cancelar", (dialog, which) -> {
                            dialogCarregamento.dismiss();
                            dialog.dismiss();
                        });
                        builder.show();
                    }
                }

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            }))));

            comentariosAdapter = new ComentariosAdapter(listaComentarios, getApplicationContext());
            recyclerViewComentarios.setAdapter(comentariosAdapter);

            buscarComentarios(idPost);

            Button buttonGostar, buttonComentar;
            buttonGostar = findViewById(R.id.buttonGostarPost);
            buttonComentar = findViewById(R.id.buttonComentarPost);


            gostosRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost).child("gostosPost");
            gostosEventListener = gostosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    dialogCarregamento.show();
                    if (snapshot.hasChild(Configs.recuperarIdUtilizador())) {
                        dialogCarregamento.dismiss();
                        buttonGostar.setText("Gostei");
                    } else {
                        dialogCarregamento.dismiss();
                        buttonGostar.setText("Gosto");
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            buttonGostar.setOnClickListener(v -> {
                DatabaseReference gostoUtilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost).child("gostosPost").child(Configs.recuperarIdUtilizador());

                if (buttonGostar.getText().toString().equals("Gostei")) {
                    gostoUtilizadorRef.removeValue().addOnCompleteListener(task -> {
                        try {
                            dialogCarregamento.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);

                        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                int gostos = snapshot.getValue(Post.class).getGostos();

                                HashMap<String, Object> hashMapGosto = new HashMap<>();
                                hashMapGosto.put("gostos", gostos - 1);

                                postRef.updateChildren(hashMapGosto).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        dialogCarregamento.dismiss();
                                        buttonGostar.setText("Gosto");
                                    } else {
                                        dialogCarregamento.dismiss();
                                        Toast.makeText(PostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                    });
                } else {
                    Gosto gosto = new Gosto();
                    gosto.setId(Configs.recuperarIdUtilizador());
                    gosto.setData(Configs.recuperarDataHoje());
                    gosto.setFoto(Configs.recuperarFotoUtilizador());
                    gosto.setNome(Configs.recuperarNomeUtilizador());

                    gostoUtilizadorRef.setValue(gosto).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            try {
                                dialogCarregamento.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);

                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    int gostos = snapshot.getValue(Post.class).getGostos();

                                    HashMap<String, Object> hashMapGosto = new HashMap<>();
                                    hashMapGosto.put("gostos", gostos + 1);

                                    postRef.updateChildren(hashMapGosto).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            dialogCarregamento.dismiss();
                                            buttonGostar.setText("Gostei");
                                        } else {
                                            dialogCarregamento.dismiss();
                                            Toast.makeText(PostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                        } else
                            Toast.makeText(PostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    });
                }

            });

            buttonComentar.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);

                EditText editText = new EditText(getApplicationContext());
                Drawable bordaPreta = ContextCompat.getDrawable(getApplicationContext(), R.drawable.borda_preta_quiz);
                Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_medium);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(16, 16, 16, 16);
                editText.setBackground(bordaPreta);
                editText.setTypeface(face);
                editText.setLayoutParams(params);
                editText.setHint("Introduza um Comentário!");

                builder.setTitle("Realização de um comentário para o Post \"" + textViewTitulo.getText() + "\"");
                builder.setMessage("Introduza algo no campo abaixo :)");
                builder.setView(editText);
                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
                builder.setPositiveButton("Confirmar", (dialog, which) -> {
                    String textoComentario = editText.getText().toString();
                    if (!textoComentario.isEmpty()) {
                        try {
                            dialogCarregamento.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        DatabaseReference comentariosRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost).child("comentariosPost");
                        String idComentario = comentariosRef.push().getKey();
                        Comentario comentario = new Comentario();
                        comentario.setId(idComentario);
                        comentario.setData(Configs.recuperarDataHoje());
                        comentario.setDescricao(textoComentario);
                        comentario.setFoto(Configs.recuperarFotoUtilizador());
                        comentario.setNome(Configs.recuperarNomeUtilizador());
                        Configs.recuperarIdUtilizador(idUtilizador -> {
                            comentario.setIdUtilizador(idUtilizador);

                            comentariosRef.child(idComentario).setValue(comentario).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);

                                    HashMap<String, Object> hashMapComentarios = new HashMap<>();

                                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            int comentarios = snapshot.getValue(Post.class).getComentarios();
                                            hashMapComentarios.put("comentarios", comentarios + 1);

                                            postRef.updateChildren(hashMapComentarios).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Toast.makeText(PostActivity.this, "Comentário criado com Sucesso!", Toast.LENGTH_SHORT).show();
                                                    dialogCarregamento.dismiss();
                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(PostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                                    dialogCarregamento.dismiss();
                                                    dialog.dismiss();
                                                }

                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });

                                } else
                                    Toast.makeText(PostActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            });

                        });


                    } else
                        Toast.makeText(PostActivity.this, "Introduza um comentário!", Toast.LENGTH_SHORT).show();

                });
                builder.show();

            });

            buscarPost(idPost);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            gostosRef.removeEventListener(gostosEventListener);
            comentariosRef.removeEventListener(comentariosEventListener);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void buscarComentarios(String idPost) {
        comentariosRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost).child("comentariosPost");
        comentariosEventListener = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialogCarregamento.show();
                listaComentarios.clear();
                for (DataSnapshot dados : snapshot.getChildren())
                    listaComentarios.add(dados.getValue(Comentario.class));
                comentariosAdapter.notifyDataSetChanged();
                dialogCarregamento.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void buscarPost(String idPost) {
        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Post post = snapshot.getValue(Post.class);

                Glide.with(getApplicationContext()).load(post.getImagem()).into(imagemFoto);
                textViewTitulo.setText(post.getTitulo());
                textViewTag.setText(post.getTag());
                textViewDescricao.setText(post.getDescricao());

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}