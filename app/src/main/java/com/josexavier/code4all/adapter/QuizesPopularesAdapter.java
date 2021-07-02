package com.josexavier.code4all.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.QuizInfoActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Quiz;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class QuizesPopularesAdapter extends RecyclerView.Adapter<QuizesPopularesAdapter.MyViewHolder> {

    private List<Quiz> listaQuizes;
    private Context context;

    public QuizesPopularesAdapter(List<Quiz> quizes, Context context) {
        this.listaQuizes = quizes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.quizes_populares_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Quiz quiz = listaQuizes.get(position);

        Glide.with(context).load(quiz.getImagem()).into(holder.imagem);
        holder.titulo.setText(quiz.getTitulo());
        holder.criadorQuiz.setText(quiz.getCriador());
        holder.dataCriacao.setText(quiz.getDataCriacao());
        holder.estrelasTotal.setText("Pontuação:\n" + String.format("%.2f", quiz.getClassificacao()));
        holder.membros.setText("Membros:\n" + quiz.getTotalMembros());

        holder.imagem.setOnClickListener(v -> {

            Intent activityQuizInfo = new Intent(context, QuizInfoActivity.class);
            activityQuizInfo.putExtra("idQuiz", quiz.getId());
            activityQuizInfo.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Necessário visto que vai ser chamada uma atividade fora do Fragment (InicioFragment)
            context.startActivity(activityQuizInfo);

        });

        Drawable estrelaAmarela = ContextCompat.getDrawable(context, R.drawable.ic_estrela_amarela);

        if ((quiz.getClassificacao() > 1 && quiz.getClassificacao() < 2) || quiz.getClassificacao() == 1) {
            holder.estrela1.setImageDrawable(estrelaAmarela);
        } else if ((quiz.getClassificacao() > 2 && quiz.getClassificacao() < 3) || quiz.getClassificacao() == 2) {
            holder.estrela1.setImageDrawable(estrelaAmarela);
            holder.estrela2.setImageDrawable(estrelaAmarela);
        } else if ((quiz.getClassificacao() > 3 && quiz.getClassificacao() < 4) || quiz.getClassificacao() == 3) {
            holder.estrela1.setImageDrawable(estrelaAmarela);
            holder.estrela2.setImageDrawable(estrelaAmarela);
            holder.estrela3.setImageDrawable(estrelaAmarela);
        } else if ((quiz.getClassificacao() > 4 && quiz.getClassificacao() < 5) || quiz.getClassificacao() == 4) {
            holder.estrela1.setImageDrawable(estrelaAmarela);
            holder.estrela2.setImageDrawable(estrelaAmarela);
            holder.estrela3.setImageDrawable(estrelaAmarela);
            holder.estrela4.setImageDrawable(estrelaAmarela);
        } else if (quiz.getClassificacao() == 5) {
            holder.estrela1.setImageDrawable(estrelaAmarela);
            holder.estrela2.setImageDrawable(estrelaAmarela);
            holder.estrela3.setImageDrawable(estrelaAmarela);
            holder.estrela4.setImageDrawable(estrelaAmarela);
            holder.estrela5.setImageDrawable(estrelaAmarela);
        }

        try {
            String id = Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getUid();
            DatabaseReference quizesSubscritos = DefinicaoFirebase.recuperarBaseDados().child("contas").child(id).child("inscricoes");
            AlertDialog dialog = new SpotsDialog.Builder().setContext(context).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
            quizesSubscritos.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (DataSnapshot dados : snapshot.getChildren()) {
                        String idQuiz = dados.getKey();

                        if (quiz.getId().equals(idQuiz)) {
                            holder.matricular.setText("Matriculado");
                            Drawable retanguloMatriculado = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja_escuro);
                            holder.matricular.setBackground(retanguloMatriculado);
                            break;
                        } else {
                            holder.matricular.setText("Matricular");
                            Drawable retanguloMatricular = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja);
                            holder.matricular.setBackground(retanguloMatricular);
                        }
                    }

                    holder.matricular.setOnClickListener(v -> {
                        if (holder.matricular.getText() != "Matriculado") {
                            FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();

                            String idUtilizador = Objects.requireNonNull(autenticacao.getCurrentUser()).getUid();
                            String idQuiz = listaQuizes.get(position).getId();
                            String tituloQuiz = listaQuizes.get(position).getTitulo();
                            String imagemQuiz = listaQuizes.get(position).getImagem();
                            String criadorQuiz = listaQuizes.get(position).getCriador();
                            String temaQuiz = listaQuizes.get(position).getTema();
                            int pontuacao = listaQuizes.get(position).getTotalXP();
                            int progresso = listaQuizes.get(position).getProgresso();
                            int totalPerguntasQuiz = listaQuizes.get(position).getTotalPerguntas();

                            DatabaseReference subscreverQuiz = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador).child("inscricoes").child(idQuiz);

                            Quiz quizSubscrito = new Quiz();

                            quizSubscrito.setId(idQuiz);
                            quizSubscrito.setTitulo(tituloQuiz);
                            quizSubscrito.setImagem(imagemQuiz);
                            quizSubscrito.setPerguntaAtual(0);
                            quizSubscrito.setTotalPerguntas(totalPerguntasQuiz);
                            quizSubscrito.setTema(temaQuiz);
                            quizSubscrito.setProgresso(progresso);
                            quizSubscrito.setDataInscricao(Configs.recuperarDataHoje());
                            quizSubscrito.setCriador(criadorQuiz);
                            quizSubscrito.setPontuacao(pontuacao);
                            quizSubscrito.guardar(subscreverQuiz, sucesso -> {
                                if (sucesso) {

                                    DatabaseReference quizRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(quiz.getId());
                                    HashMap<String, Object> hashMapTotalMembros = new HashMap<>();

                                    quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            try {
                                                int totalMembros = snapshot.child("totalMembros").getValue(Integer.class);
                                                hashMapTotalMembros.put("totalMembros", totalMembros + 1);

                                                quizRef.updateChildren(hashMapTotalMembros).addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Drawable retanguloMatriculado = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja_escuro);
                                                        holder.matricular.setBackground(retanguloMatriculado);
                                                        holder.matricular.setText("Matriculado");
                                                    } else
                                                        Toast.makeText(context, context.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                } else
                                    Toast.makeText(context, context.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            });

                        }

                    });

                    if (!snapshot.hasChildren()) {
                        holder.matricular.setText("Matricular");
                        Drawable retanguloMatricular = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja);
                        holder.matricular.setBackground(retanguloMatricular);
                    }

                    dialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listaQuizes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem, estrela1, estrela2, estrela3, estrela4, estrela5;
        private TextView titulo, criadorQuiz, dataCriacao, estrelasTotal, membros;
        private Button matricular;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imagemQuiz);
            titulo = itemView.findViewById(R.id.nomeQuiz);
            criadorQuiz = itemView.findViewById(R.id.criadorQuiz);
            dataCriacao = itemView.findViewById(R.id.dataCriacaoQuiz);
            estrelasTotal = itemView.findViewById(R.id.estrelasTotal);
            matricular = itemView.findViewById(R.id.botaoMatricularQuiz);
            membros = itemView.findViewById(R.id.textViewMembrosQuiz);

            estrela1 = itemView.findViewById(R.id.estrelaQuiz1);
            estrela2 = itemView.findViewById(R.id.estrelaQuiz2);
            estrela3 = itemView.findViewById(R.id.estrelaQuiz3);
            estrela4 = itemView.findViewById(R.id.estrelaQuiz4);
            estrela5 = itemView.findViewById(R.id.estrelaQuiz5);

        }
    }
}
