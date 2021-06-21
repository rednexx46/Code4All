package com.josexavier.code4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.model.Comentario;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.MyViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public ComentariosAdapter(List<Comentario> comentarios, Context c) {
        this.listaComentarios = comentarios;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.comentarios_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Comentario comentario = listaComentarios.get(position);

        Glide.with(context).load(comentario.getFoto()).into(holder.imagem);
        holder.autor.setText(comentario.getNome());
        holder.data.setText(comentario.getData());
        holder.descricao.setText(comentario.getDescricao());

    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imagem;
        private TextView autor, data, descricao;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewFotoComentarios);
            autor = itemView.findViewById(R.id.textViewAutorComentarios);
            data = itemView.findViewById(R.id.textViewDataComentarios);
            descricao = itemView.findViewById(R.id.textViewDescricaoComentarios);

        }
    }

}
