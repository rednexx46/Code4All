package com.josexavier.code4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Post;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsVerificacaoAdapter extends RecyclerView.Adapter<PostsVerificacaoAdapter.MyViewHolder> {

    private Context context;
    private List<Post> listaPosts;

    public PostsVerificacaoAdapter(List<Post> posts, Context c) {
        this.context = c;
        this.listaPosts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.posts_verificacao_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Post post = listaPosts.get(position);

        Glide.with(context).load(post.getImagem()).into(holder.imagem);
        holder.criador.setText(post.getCriador());
        holder.titulo.setText(post.getTitulo());
        holder.descricao.setText(post.getDescricao());
        holder.tag.setText(post.getTag());
        holder.botaoAceitar.setOnClickListener(v -> atualizarEstado(listaPosts.get(position).getId(), Configs.ACEITE));
        holder.botaoRecusar.setOnClickListener(v -> atualizarEstado(listaPosts.get(position).getId(), Configs.RECUSADO));

    }

    @Override
    public int getItemCount() {
        return listaPosts.size();
    }

    private void atualizarEstado(String idPost, String estado) {

        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(idPost);

        if (estado.equals("aceite")) { // se for aceite, atualiza o estado da mesma...
            postRef.child("estado").setValue(estado).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    Toast.makeText(context, "Postagem aceite com Sucesso!", Toast.LENGTH_SHORT).show();
                }
            });
        } else { // se nao for, remove o post por completo...
            postRef.removeValue().addOnCompleteListener(task -> Toast.makeText(context, "Postagem removida com Sucesso!", Toast.LENGTH_SHORT).show());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imagem;
        private TextView criador, titulo, descricao, tag;
        private ImageButton botaoAceitar, botaoRecusar;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewPostVerificao);
            criador = itemView.findViewById(R.id.textPostVerificacaoCriador);
            titulo = itemView.findViewById(R.id.textPostVerificacaoTitulo);
            descricao = itemView.findViewById(R.id.textDescricaoPostVerificacao);
            tag = itemView.findViewById(R.id.textPostVerificacaoTag);
            botaoAceitar = itemView.findViewById(R.id.imageButtonAceitarPost);
            botaoRecusar = itemView.findViewById(R.id.imageButtonRecusarPost);

        }
    }

}
