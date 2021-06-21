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
import com.josexavier.code4all.model.Conta;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContasAdminAdapter extends RecyclerView.Adapter<ContasAdminAdapter.MyViewHolder> {

    private List<Conta> listaContas;
    private Context context;

    public ContasAdminAdapter(List<Conta> contas, Context c) {
        this.listaContas = contas;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override

    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.contas_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Conta conta = listaContas.get(holder.getAdapterPosition());

        Glide.with(context).load(conta.getFoto()).into(holder.imagem);
        holder.textNome.setText(listaContas.get(position).getNome());
        holder.textTipo.setText(listaContas.get(position).getTipo());
        holder.textEmail.setText(listaContas.get(position).getEmail());

    }

    public void filtrarDados(List<Conta> listaFiltrada) {
        listaContas = new ArrayList<>();
        listaContas.addAll(listaFiltrada);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaContas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textNome, textTipo, textEmail;
        private ImageView imagem;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewConta);
            textNome = itemView.findViewById(R.id.textViewNomeConta);
            textTipo = itemView.findViewById(R.id.textViewTipoConta);
            textEmail = itemView.findViewById(R.id.textViewEmailConta);

        }
    }

}
