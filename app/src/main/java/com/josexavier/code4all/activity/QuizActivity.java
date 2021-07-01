package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.interfaces.Validacao;
import com.josexavier.code4all.model.Conta;
import com.josexavier.code4all.model.Pergunta;
import com.josexavier.code4all.model.Quiz;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class QuizActivity extends AppCompatActivity {

    private Quiz quizAtual, quizAtualInscrito;

    private ProgressBar progressBarQuiz;
    private TextView textViewProgresso, textViewPergunta, textViewSolucaoMultipla;
    private Button botaoConfirmar, botaoLimpar;
    private String solucaoMultipla = "";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        String idQuiz = getIntent().getStringExtra("idQuiz");


        progressBarQuiz = findViewById(R.id.progressBarProgressoQuiz);

        textViewProgresso = findViewById(R.id.textViewProgressoQuiz);
        textViewPergunta = findViewById(R.id.textViewPerguntaQuiz);
        textViewSolucaoMultipla = findViewById(R.id.textViewSolucaoMultiplaQuiz);

        botaoConfirmar = findViewById(R.id.buttonConfirmarSelecaoQuiz);
        botaoLimpar = findViewById(R.id.buttonLimparSelecaoQuiz);

        buscarQuiz(idQuiz, validar -> {
            if (validar) {
                buscarInfoQuizAtual(validar2 -> {
                    if (validar2) {
                        DatabaseReference quizAtualRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(Configs.recuperarIdUtilizador()).child("inscricoes").child(quizAtual.getId());
                        dialog.dismiss();
                        preparacaoInterface(quizAtualRef);
                    } else {
                        dialog.dismiss();
                        Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

    }

    private void preparacaoInterface(DatabaseReference quizAtualRef) {
        List<Pergunta> perguntas = quizAtual.getPerguntas();
        int perguntaAtual = quizAtualInscrito.getPerguntaAtual();
        textViewProgresso.setText("Pergunta " + (perguntaAtual + 1) + " de " + quizAtual.getTotalPerguntas());
        progressBarQuiz.setMax(quizAtual.getTotalPerguntas());
        progressBarQuiz.setProgress(perguntaAtual);

        LinearLayout linearLayoutMultipla = findViewById(R.id.linearLayoutMultipla);
        RadioGroup radioGroup = findViewById(R.id.radioGroupOpcoes);

        if (perguntas.get(perguntaAtual).getTipo().equals("unica")) {
            radioGroup.setVisibility(View.VISIBLE);
            linearLayoutMultipla.setVisibility(View.GONE);
            botaoLimpar.setVisibility(View.GONE);

            int alfabeto = 65;

            for (int i = 0; i < perguntas.get(perguntaAtual).getOpcoesPergunta().size(); i++) {
                char caracter = (char) alfabeto;
                String botaoID = "radioButtonOpcao" + caracter;
                int resID = getResources().getIdentifier(botaoID, "id", getPackageName());
                RadioButton radioButton = findViewById(resID);
                radioButton.setVisibility(View.VISIBLE);
                radioButton.setText(radioButton.getTag().toString() + " - " + perguntas.get(perguntaAtual).getOpcoesPergunta().get(i));
                alfabeto++;

            }

            while (alfabeto < 71) {
                char caracter = (char) alfabeto;
                String botaoID = "radioButtonOpcao" + caracter;
                int resID = getResources().getIdentifier(botaoID, "id", getPackageName());
                RadioButton radioButton = findViewById(resID);
                radioButton.setVisibility(View.GONE);
                alfabeto++;
            }

            botaoConfirmar.setOnClickListener(v -> {
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Selecione primeiro uma opção!", Toast.LENGTH_SHORT).show();
                } else {
                    int idBotao = radioGroup.getCheckedRadioButtonId();
                    String resposta = findViewById(idBotao).getTag().toString();
                    String solucao = perguntas.get(perguntaAtual).getSolucao();

                    if (resposta.equals(solucao)) {
                        int xpPergunta = perguntas.get(perguntaAtual).getXp();
                        HashMap<String, Object> hashMapXP = new HashMap<>();

                        quizAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Quiz quiz = snapshot.getValue(Quiz.class);
                                hashMapXP.put("totalXP", Objects.requireNonNull(quiz).getTotalXP() + xpPergunta);
                                quizAtualRef.updateChildren(hashMapXP).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        limparRadioButtons();
                                        Toast.makeText(QuizActivity.this, "Resposta Certa :)", Toast.LENGTH_SHORT).show();
                                        proximaPergunta(perguntaAtual, quizAtualRef);
                                    } else
                                        Toast.makeText(QuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(QuizActivity.this, "Resposta Errada :(", Toast.LENGTH_SHORT).show();
                        proximaPergunta(perguntaAtual, quizAtualRef);
                    }
                    radioGroup.check(-1);
                }

            });

        } else if (perguntas.get(perguntaAtual).getTipo().equals("multipla")) {
            linearLayoutMultipla.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.GONE);
            botaoLimpar.setVisibility(View.VISIBLE);
            textViewSolucaoMultipla.setVisibility(View.VISIBLE);

            int alfabeto = 65;

            for (int i = 0; i < perguntas.get(perguntaAtual).getOpcoesPergunta().size(); i++) {
                char caracter = (char) alfabeto;
                String botaoID = "checkBox" + caracter;
                int resID = getResources().getIdentifier(botaoID, "id", getPackageName());
                CheckBox checkBox = findViewById(resID);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setText(checkBox.getTag().toString() + " - " + perguntas.get(perguntaAtual).getOpcoesPergunta().get(i));
                alfabeto++;
            }

            while (alfabeto < 71) {
                char caracter = (char) alfabeto;
                String botaoID = "checkBox" + caracter;
                int resID = getResources().getIdentifier(botaoID, "id", getPackageName());
                CheckBox checkBox = findViewById(resID);
                checkBox.setVisibility(View.GONE);
                alfabeto++;
            }

            botaoConfirmar.setOnClickListener(v -> {

                String solucaoQuiz = quizAtual.getPerguntas().get(perguntaAtual).getSolucao();

                if (solucaoMultipla.equals("") || solucaoMultipla.length() < solucaoQuiz.length())
                    Toast.makeText(this, "É preciso selecionar " + solucaoQuiz.length() + " opções!", Toast.LENGTH_SHORT).show();
                else if (solucaoMultipla.length() > solucaoQuiz.length())
                    Toast.makeText(this, "Está a selecionar mais opções que o devido!", Toast.LENGTH_SHORT).show();
                else {

                    int count = 0;

                    char[] solucaoUtilizador = new char[solucaoMultipla.length()];
                    char[] solucaoQuizFinal = new char[solucaoQuiz.length()];

                    for (int i = 0; i < solucaoMultipla.length(); i++) {
                        solucaoUtilizador[i] = solucaoMultipla.charAt(i);
                    }

                    for (int i = 0; i < solucaoQuiz.length(); i++) {
                        solucaoQuizFinal[i] = solucaoQuiz.charAt(i);
                    }

                    for (int i = 0; i < solucaoQuizFinal.length; i++) {

                        for (int j = 0; j < solucaoUtilizador.length; j++) {
                            if (solucaoUtilizador[j] == solucaoQuizFinal[i]) {
                                count++;
                            }

                        }
                    }

                    int xpPergunta = perguntas.get(perguntaAtual).getXp();

                    if (count > 0 && count < solucaoMultipla.length()) {
                        double desconto = ((double) count / solucaoMultipla.length());
                        xpPergunta = (int) Math.round(xpPergunta * desconto);
                    } else if (count == 0) {
                        xpPergunta = 0;
                    }

                    HashMap<String, Object> hashMapXP = new HashMap<>();
                    int finalXpPergunta = xpPergunta;

                    quizAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Quiz quiz = snapshot.getValue(Quiz.class);
                            hashMapXP.put("totalXP", Objects.requireNonNull(quiz).getTotalXP() + finalXpPergunta);

                            quizAtualRef.updateChildren(hashMapXP).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    solucaoMultipla = "";
                                    Toast.makeText(QuizActivity.this, "Resposta Guardada com Sucesso!", Toast.LENGTH_SHORT).show();
                                    proximaPergunta(perguntaAtual, quizAtualRef);
                                } else
                                    Toast.makeText(QuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }

            });

        }

        textViewPergunta.setText(perguntas.get(perguntaAtual).titulo);

    }

    private void proximaPergunta(int perguntaAtual, DatabaseReference quizAtualRef) {
        HashMap<String, Object> hashMapPerguntaAtual = new HashMap<>();

        hashMapPerguntaAtual.put("perguntaAtual", perguntaAtual + 1);
        int totalPerguntas = quizAtual.getTotalPerguntas();
        float progresso = ((float) (perguntaAtual + 1) / totalPerguntas);
        int progressoFinal = (int) (progresso * 100);
        hashMapPerguntaAtual.put("progresso", progressoFinal);

        quizAtualRef.updateChildren(hashMapPerguntaAtual).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                limparTudo();
                buscarInfoQuizAtual(validar2 -> {
                    if (validar2)
                        if (progressoFinal == 100) {

                            quizAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    int xpQuiz = Objects.requireNonNull(snapshot.getValue(Quiz.class)).getTotalXP();


                                    DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(Configs.recuperarIdUtilizador());

                                    utilizadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            Conta conta = snapshot.getValue(Conta.class);

                                            HashMap<String, Object> hashMapXP = new HashMap<>();
                                            hashMapXP.put("totalXP", Objects.requireNonNull(conta).getTotalXP() + xpQuiz); // somar totalxp mais o xp adquirido no quiz feito

                                            utilizadorRef.updateChildren(hashMapXP).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Intent activityQuizAvaliacao = new Intent(getApplicationContext(), AvaliacaoQuizActivity.class);
                                                    activityQuizAvaliacao.putExtra("idQuiz", quizAtual.getId());
                                                    startActivity(activityQuizAvaliacao);
                                                    finish();
                                                } else
                                                    Toast.makeText(QuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                            });


                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                        } else {
                            preparacaoInterface(quizAtualRef);
                        }

                    else
                        Toast.makeText(QuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                });
            } else
                Toast.makeText(QuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
        });
    }

    private void limparTudo() {

        limparRadioButtons();
        limparCheckBoxs();

        botaoLimpar.setVisibility(View.GONE);
        textViewSolucaoMultipla.setVisibility(View.GONE);

    }

    private void limparCheckBoxs() {
        CheckBox checkBoxA = findViewById(R.id.checkBoxA);
        checkBoxA.setChecked(false);

        CheckBox checkBoxB = findViewById(R.id.checkBoxB);
        checkBoxB.setChecked(false);

        CheckBox checkBoxC = findViewById(R.id.checkBoxC);
        checkBoxC.setChecked(false);

        CheckBox checkBoxD = findViewById(R.id.checkBoxD);
        checkBoxD.setChecked(false);

        CheckBox checkBoxE = findViewById(R.id.checkBoxE);
        checkBoxE.setChecked(false);

        CheckBox checkBoxF = findViewById(R.id.checkBoxF);
        checkBoxF.setChecked(false);

    }

    private void limparRadioButtons() {
        RadioButton radioButtonA = findViewById(R.id.radioButtonOpcaoA);
        radioButtonA.setChecked(false);
        radioButtonA.setText("");

        RadioButton radioButtonB = findViewById(R.id.radioButtonOpcaoB);
        radioButtonB.setChecked(false);
        radioButtonB.setText("");

        RadioButton radioButtonC = findViewById(R.id.radioButtonOpcaoC);
        radioButtonC.setChecked(false);
        radioButtonC.setText("");

        RadioButton radioButtonD = findViewById(R.id.radioButtonOpcaoD);
        radioButtonD.setChecked(false);
        radioButtonD.setText("");

        RadioButton radioButtonE = findViewById(R.id.radioButtonOpcaoE);
        radioButtonE.setChecked(false);
        radioButtonE.setText("");

        RadioButton radioButtonF = findViewById(R.id.radioButtonOpcaoF);
        radioButtonF.setChecked(false);
        radioButtonF.setText("");
    }

    private void buscarInfoQuizAtual(Validacao validacao) {
        DatabaseReference quizAtualRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(Configs.recuperarIdUtilizador()).child("inscricoes").child(quizAtual.getId());
        quizAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                quizAtualInscrito = snapshot.getValue(Quiz.class);
                validacao.isValidacaoSucesso(true);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void buscarQuiz(String idQuiz, Validacao validacao) {
        dialog.show();
        DatabaseReference quizRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(idQuiz);
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                quizAtual = snapshot.getValue(Quiz.class);
                validacao.isValidacaoSucesso(true);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void adicionarSolucao(View view) {
        solucaoMultipla = solucaoMultipla + view.getTag().toString();
        textViewSolucaoMultipla.setVisibility(View.VISIBLE);
        textViewSolucaoMultipla.setText("Solução Atual .: " + solucaoMultipla);
    }

    public void limparSolucaoMultipla(View view) {
        solucaoMultipla = "";
        limparCheckBoxs();
        Toast.makeText(this, "Solução Limpa com Sucesso!", Toast.LENGTH_SHORT).show();
        textViewSolucaoMultipla.setText("");
    }

}