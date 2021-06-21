package com.josexavier.code4all.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.model.Notificacao;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificacoesAdapter extends RecyclerView.Adapter<NotificacoesAdapter.MyViewHolder> {

    private List<Notificacao> listaNotificacoes;
    private Context context;

    public NotificacoesAdapter(List<Notificacao> notificacoes, Context c) {
        this.listaNotificacoes = notificacoes;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.notificacoes_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Notificacao notificacao = listaNotificacoes.get(position);

        Glide.with(context).load(notificacao.getImagem()).into(holder.imagem);
        holder.titulo.setText(notificacao.getTitulo());
        holder.descricao.setText(notificacao.getDescricao());
        holder.data.setText(notificacao.getData());

    }

    @Override
    public int getItemCount() {
        return listaNotificacoes.size();
    }

    public void filtrarDados(List<Notificacao> listaFiltrada) {
        listaNotificacoes = listaFiltrada;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView titulo, descricao, data;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewNotificacoes);
            titulo = itemView.findViewById(R.id.textViewTituloNotificacoes);
            descricao = itemView.findViewById(R.id.textViewDescricaoNotificacoes);
            data = itemView.findViewById(R.id.textViewDataNotificacoes);

        }
    }

}
