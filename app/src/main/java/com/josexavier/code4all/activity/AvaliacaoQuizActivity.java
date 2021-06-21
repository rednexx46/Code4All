package com.josexavier.code4all.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Quiz;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AvaliacaoQuizActivity extends AppCompatActivity {

    private boolean isBotaoClicado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao_quiz);

        String idQuiz = getIntent().getStringExtra("idQuiz");

        EditText editTextAvaliacao;
        editTextAvaliacao = findViewById(R.id.editTextAvaliacaoQuiz);

        FloatingActionButton fabAvaliacao;
        fabAvaliacao = findViewById(R.id.fabAvaliacaoQuiz);

        fabAvaliacao.setOnClickListener(v -> {
            DatabaseReference quizAtualRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(idQuiz);

            String avaliacao = editTextAvaliacao.getText().toString();

            if (!avaliacao.isEmpty()) {
                if (Double.valueOf(avaliacao) > 0) {


                    quizAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Quiz quiz = snapshot.getValue(Quiz.class);

                            double classificacao = quiz.getClassificacao();
                            double classificaoFinal = (classificacao + Double.valueOf(avaliacao)) / 2;

                            HashMap<String, Object> hashClassificacao = new HashMap<>();

                            hashClassificacao.put("classificacao", classificaoFinal);

                            quizAtualRef.updateChildren(hashClassificacao).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AvaliacaoQuizActivity.this, "Avaliação submetida com Sucesso!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    if (isBotaoClicado)
                                        startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
                                } else
                                    Toast.makeText(AvaliacaoQuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                } else
                    Toast.makeText(this, "Introduza uma avaliação maior que 0!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Introduza uma avaliação primeiro!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void partilharLinkedIn(View view) {
        isBotaoClicado = true;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.linkedin.com/sharing/share-offsite/?url=http://code4all.duckdns.org"));
        startActivity(i);
    }
}