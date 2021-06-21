package com.josexavier.code4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.ContaActivity;
import com.josexavier.code4all.activity.CriarOfertaActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Conta;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ContasEmpresaAdapter extends RecyclerView.Adapter<ContasEmpresaAdapter.MyViewHolder> {

    private List<Conta> listaContas;
    private Context context;

    public ContasEmpresaAdapter(List<Conta> lista, Context c) {
        this.listaContas = lista;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.contas_empresa_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Conta conta = listaContas.get(position);

        Glide.with(context).load(conta.getFoto()).into(holder.imagem);

        SimpleDateFormat dataFormatacao = new SimpleDateFormat("dd/MM/yy");

        int idade = 0;

        // Cálculo da idade consoante a sua data de nascimento e a data atual
        try {
            Calendar calendarioHoje = Calendar.getInstance();
            calendarioHoje.setTime(dataFormatacao.parse(Configs.recuperarDataHoje()));

            Calendar calendarioNascimento = Calendar.getInstance();
            calendarioNascimento.setTime(dataFormatacao.parse(conta.getDataNascimento()));

            if (calendarioHoje.before(calendarioNascimento)) {
                idade = (calendarioHoje.get(Calendar.YEAR) - calendarioNascimento.get(Calendar.YEAR));
            } else {
                idade = (calendarioHoje.get(Calendar.YEAR) - calendarioNascimento.get(Calendar.YEAR)) - 1;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Abrir a atividade responsável por apresentar os dados da conta do Utilizador
        holder.imagem.setOnClickListener(v -> {
            Intent intentContaActivity = new Intent(context, ContaActivity.class);
            intentContaActivity.putExtra("idConta", conta.getId());
            intentContaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intentContaActivity);
        });

        // Abrir a atividade responsável pela a criação de uma oferta para o Utilizador Selecionado
        holder.botaoSelecionar.setOnClickListener(v -> {
            Intent intentCriarOfertaActivity = new Intent(context, CriarOfertaActivity.class);
            intentCriarOfertaActivity.putExtra("idConta", conta.getId());
            intentCriarOfertaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intentCriarOfertaActivity);
        });

        holder.nome.setText(conta.getNome());
        holder.idade.setText(idade + " anos");
        holder.estado.setText(conta.getEstado());
        holder.xp.setText("Total XP .: " + conta.getTotalXP());

    }

    @Override
    public int getItemCount() {
        return listaContas.size();
    }

    public void filtrarDados(List<Conta> listaFiltrada) {
        listaContas = listaFiltrada;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView nome, idade, estado, xp;
        private Button botaoSelecionar;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // Configurações Iniciais
            nome = itemView.findViewById(R.id.textViewNomeContasEmpresa);
            imagem = itemView.findViewById(R.id.imageViewFotoContasEmpresa);
            idade = itemView.findViewById(R.id.textViewIdadeContasEmpresa);
            estado = itemView.findViewById(R.id.textViewEstadoContasEmpresa);
            xp = itemView.findViewById(R.id.textViewXPContasEmpresa);
            botaoSelecionar = itemView.findViewById(R.id.buttonSelecionarContaEmpresas);

        }
    }

}
