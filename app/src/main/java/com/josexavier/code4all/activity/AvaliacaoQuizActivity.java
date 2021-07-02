package com.josexavier.code4all.activity;

import android.app.AlertDialog;
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
import java.util.Objects;

import dmax.dialog.SpotsDialog;

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

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Avaliação...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        fabAvaliacao.setOnClickListener(v -> {

            try {

                DatabaseReference quizAtualRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(idQuiz);

                String avaliacao = editTextAvaliacao.getText().toString();
                double avalia = Double.parseDouble(avaliacao);

                if (!avaliacao.isEmpty()) {
                    if (avalia > 0 && avalia <= 5) {
                        try {
                            dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        quizAtualRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Quiz quiz = snapshot.getValue(Quiz.class);

                                double classificacao = Objects.requireNonNull(quiz).getClassificacao();
                                double classificaoFinal = (classificacao + avalia) / 2;

                                HashMap<String, Object> hashClassificacao = new HashMap<>();

                                hashClassificacao.put("classificacao", classificaoFinal);

                                quizAtualRef.updateChildren(hashClassificacao).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(AvaliacaoQuizActivity.this, "Avaliação submetida com Sucesso!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        if (isBotaoClicado)
                                            startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(AvaliacaoQuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    } else
                        Toast.makeText(this, "Introduza uma avaliação maior que 0 e menor que 5!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Introduza uma avaliação primeiro!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
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