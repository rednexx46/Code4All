package com.josexavier.code4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.bumptech.glide.Glide;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private List<Post> listaPost;
    private Context context;

    public PostsAdapter(List<Post> post, Context c) {
        this.listaPost = post;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.forum_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = listaPost.get(position);

        Glide.with(context).load(post.getImagem()).into(holder.imagem);
        holder.data.setText(post.getDataCriacao());
        holder.titulo.setText(post.getTitulo());
        holder.criador.setText(post.getCriador());
        holder.tema.setText(post.getTag());
        holder.comentarios.setText(String.valueOf(post.getComentarios()));
        holder.gostos.setText(String.valueOf(post.getGostos()));

        if (post.getEstado().equals(Configs.ACEITE))
            holder.estado.setVisibility(View.GONE);
        else if (post.getEstado().equals(Configs.PENDENTE)) {
            holder.estado.setText("Pendente");
            holder.estado.setVisibility(View.VISIBLE);
            int corLaranja = ContextCompat.getColor(context, R.color.corPrimariaEscura);
            holder.estado.setTextColor(corLaranja);
        }

    }

    @Override
    public int getItemCount() {
        return listaPost.size();
    }

    public void filtrarDados(List<Post> listaFiltrada) {
        listaPost = listaFiltrada;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView data, titulo, criador, comentarios, gostos, tema, estado;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageForum);
            data = itemView.findViewById(R.id.textForumData);
            titulo = itemView.findViewById(R.id.textForumTitulo);
            criador = itemView.findViewById(R.id.textForumCriador);
            tema = itemView.findViewById(R.id.textForumTag);
            comentarios = itemView.findViewById(R.id.textForumComentarios);
            gostos = itemView.findViewById(R.id.textForumGostos);
            estado = itemView.findViewById(R.id.textEstadoForum);

        }
    }

}
