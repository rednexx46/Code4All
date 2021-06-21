package com.josexavier.code4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.QuizActivity;
import com.josexavier.code4all.activity.QuizInfoActivity;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Quiz;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuizesSubscritosAdapter extends RecyclerView.Adapter<QuizesSubscritosAdapter.MyViewHolder> {

    private List<Quiz> listaQuizes;
    private Context context;

    public QuizesSubscritosAdapter(List<Quiz> quizes, Context context) {
        this.listaQuizes = quizes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.quizes_subscritos_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Quiz quizSubscrito = listaQuizes.get(position);

        Glide.with(context).load(quizSubscrito.getImagem()).into(holder.imagemQuiz);
        holder.tituloQuiz.setText(quizSubscrito.getTitulo());
        holder.barraProgressoQuiz.setProgress(quizSubscrito.getProgresso());
        holder.progressoQuiz.setText("Progresso (" + quizSubscrito.getProgresso() + "%)");
        holder.criadorQuiz.setText("Criador .: " + quizSubscrito.getCriador());
        holder.dataInscricaoQuiz.setText("Inscrito em .: " + quizSubscrito.getDataInscricao());

        holder.imagemQuiz.setOnClickListener(v -> {

            Quiz quiz = listaQuizes.get(position);

            Intent activityQuizInfo = new Intent(context, QuizInfoActivity.class);
            activityQuizInfo.putExtra("idQuiz", quiz.getId());
            activityQuizInfo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activityQuizInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessário visto que vai ser chamada uma atividade fora do Fragment (InicioFragment)
            context.startActivity(activityQuizInfo);

        });

        holder.botaoQuiz.setOnClickListener(v -> {
            Quiz quiz = listaQuizes.get(position);

            Intent activityQuizInfo = new Intent(context, QuizActivity.class);
            activityQuizInfo.putExtra("idQuiz", quiz.getId());
            activityQuizInfo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activityQuizInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessário visto que vai ser chamada uma atividade fora do Fragment (InicioFragment)
            context.startActivity(activityQuizInfo);
        });

        if (quizSubscrito.getPerguntaAtual() == 0) {
            holder.botaoQuiz.setText("Começar!");
        } else if (quizSubscrito.getProgresso() == 100) {
            holder.botaoQuiz.setVisibility(View.GONE);
            holder.pontuacao.setVisibility(View.VISIBLE);
            holder.progressoQuiz.setVisibility(View.GONE);
            holder.barraProgressoQuiz.setVisibility(View.GONE);
            DatabaseReference quizRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(quizSubscrito.getId());
            quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    int totalXP = snapshot.getValue(Quiz.class).getTotalXP();
                    double pontuacao = (100 * quizSubscrito.getTotalXP()) / totalXP; // Regra 3 Simples #REGRA3SIMPLESEVIDA
                    holder.pontuacao.setText("Pontuação Final :\n" + pontuacao + " %");
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        } else {
            holder.botaoQuiz.setText("Continuar!");
        }

    }

    @Override
    public int getItemCount() {
        return listaQuizes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagemQuiz;
        private TextView tituloQuiz, criadorQuiz, dataInscricaoQuiz, progressoQuiz, pontuacao;
        private ProgressBar barraProgressoQuiz;
        private Button botaoQuiz;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemQuiz = itemView.findViewById(R.id.imagemQuizSubscrito);
            tituloQuiz = itemView.findViewById(R.id.nomeQuizSubscrito);
            criadorQuiz = itemView.findViewById(R.id.criadorQuizSubscrito);
            dataInscricaoQuiz = itemView.findViewById(R.id.dataInscricaoQuizSubscrito);
            progressoQuiz = itemView.findViewById(R.id.progressoQuizesSubscritos);
            barraProgressoQuiz = itemView.findViewById(R.id.progressBarQuizesSubscritos);
            pontuacao = itemView.findViewById(R.id.textPontuacaoQuizSubscrito);
            botaoQuiz = itemView.findViewById(R.id.botaoEntrarQuizSubscrito);

        }
    }

}
