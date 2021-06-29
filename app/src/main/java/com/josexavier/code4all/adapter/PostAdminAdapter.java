package com.josexavier.code4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.josexavier.code4all.R;
import com.josexavier.code4all.model.Post;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostAdminAdapter extends RecyclerView.Adapter<PostAdminAdapter.MyViewHolder> {

    private List<Post> posts;
    private Context c;

    public PostAdminAdapter(List<Post> listaPosts, Context context) {
        this.posts = listaPosts;
        this.c = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.post_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Post post = posts.get(position);

        Glide.with(c).load(post.getImagem()).into(holder.imagem);
        holder.criador.setText(post.getCriador());
        holder.titulo.setText(post.getTitulo());
        holder.tag.setText(post.getTag());
        holder.comentarios.setText("Comentários .: " + post.getComentarios());
        holder.gostos.setText("Gostos .: " + post.getGostos());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void filtrarDados(List<Post> listaFiltrada) {
        posts = listaFiltrada;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView criador, titulo, tag, descricao, comentarios, gostos;
        private ImageView imagem;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewPost);
            criador = itemView.findViewById(R.id.textPostCriador);
            titulo = itemView.findViewById(R.id.textPostTitulo);
            tag = itemView.findViewById(R.id.textPostTag);
            comentarios = itemView.findViewById(R.id.textPostComentarios);
            gostos = itemView.findViewById(R.id.textPostGostos);

        }
    }

}
