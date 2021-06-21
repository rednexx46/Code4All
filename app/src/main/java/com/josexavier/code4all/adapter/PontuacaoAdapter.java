package com.josexavier.code4all.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.bumptech.glide.Glide;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Conta;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PontuacaoAdapter extends RecyclerView.Adapter<PontuacaoAdapter.MyViewHolder> {

    private final int limite = 10;
    private List<Conta> listaContas;
    private Context context;

    public PontuacaoAdapter(List<Conta> contas, Context c) {
        this.listaContas = contas;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.pontuacao_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Conta conta = listaContas.get(position);

        Glide.with(context).load(conta.getFoto()).into(holder.imagem);
        holder.nome.setText(conta.getNome());
        holder.xp.setText("XP Total : " + conta.getTotalXP());
        holder.posicao.setText("Posição #" + (position + 1));
        holder.estado.setText("Estado .: " + conta.getEstado());

        if (listaContas.get(position).getId().equals(Configs.recuperarIdUtilizador())) {
            Drawable background = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja_escuro);
            holder.linearLayout.setBackground(background);
        }


    }

    @Override
    public int getItemCount() {
        // limite -> private final int limite = 10, usado para mostrar apenas 10 valores (não sobrecarregando assim o RecyclerView)
        if (listaContas.size() > limite) {
            return limite;
        } else {
            return listaContas.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private TextView nome, xp, posicao, estado;
        private CircleImageView imagem;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayoutPontuacao);
            nome = itemView.findViewById(R.id.textViewNomePontuacao);
            xp = itemView.findViewById(R.id.textViewXPPontuacao);
            posicao = itemView.findViewById(R.id.textViewPosicaoPontuacao);
            imagem = itemView.findViewById(R.id.imagePerfilPontuacao);
            estado = itemView.findViewById(R.id.textViewEstadoPontuacao);

        }
    }

}
