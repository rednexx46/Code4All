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
import com.josexavier.code4all.model.Quiz;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuizesAdminAdapter extends RecyclerView.Adapter<QuizesAdminAdapter.MyViewHolder> {

    private List<Quiz> listaQuizes;
    private Context c;

    public QuizesAdminAdapter(List<Quiz> listaQuizes, Context context) {
        this.listaQuizes = listaQuizes;
        this.c = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.quizes_admin_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Quiz quiz = listaQuizes.get(position);

        Glide.with(c).load(quiz.getImagem()).into(holder.imagem);
        holder.titulo.setText(quiz.getTitulo());
        holder.criador.setText(quiz.getCriador());
        holder.classificacao.setText(String.format("%.2f", quiz.getClassificacao()));
        holder.membros.setText("Membros .: " + quiz.getTotalMembros());
        holder.perguntas.setText("Perguntas .: " + quiz.getTotalPerguntas());

    }

    @Override
    public int getItemCount() {
        return listaQuizes.size();
    }

    public void filtrarDados(List<Quiz> listaFiltrada) {
        listaQuizes = listaFiltrada;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView titulo, criador, classificacao, membros, perguntas;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageQuizAdminFoto);
            titulo = itemView.findViewById(R.id.textTituloAdminQuiz);
            criador = itemView.findViewById(R.id.textCriadorAdminQuiz);
            classificacao = itemView.findViewById(R.id.textClassificacaoAdminQuiz);
            membros = itemView.findViewById(R.id.textTotalMembrosAdminQuiz);
            perguntas = itemView.findViewById(R.id.textTotalPerguntasAdminQuiz);

        }
    }

}
