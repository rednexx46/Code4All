package com.josexavier.code4all.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

public class EditarQuizActivity extends AppCompatActivity {

    private ImageView imageViewEditarQuiz;
    private EditText editTextTituloQuiz, editTextTituloIntro, editTextDescricao, editTextVideo;
    private android.app.AlertDialog dialog, dialogCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_quiz);

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(this).setMessage("Guardando Alterações...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        String idQuiz = getIntent().getStringExtra("idQuiz");

        imageViewEditarQuiz = findViewById(R.id.imageViewEditarQuiz);

        editTextTituloQuiz = findViewById(R.id.editTextTituloQuizEditarQuiz);
        editTextTituloIntro = findViewById(R.id.editTextTituloIntroducaoEditarQuiz);
        editTextDescricao = findViewById(R.id.editTextDescricaoEditarQuiz);
        editTextVideo = findViewById(R.id.editTextVideoEditarQuiz);

        Button buttonConfirmarAlteracoes;
        buttonConfirmarAlteracoes = findViewById(R.id.buttonConfirmarEditarQuiz);
        buttonConfirmarAlteracoes.setOnClickListener(v -> {
            String tituloQuiz = editTextTituloQuiz.getText().toString();
            String tituloIntro = editTextTituloIntro.getText().toString();
            String descricao = editTextDescricao.getText().toString();
            String video = editTextVideo.getText().toString();

            if (!tituloQuiz.isEmpty()) {
                if (!tituloIntro.isEmpty()) {
                    if (!descricao.isEmpty()) {
                        if (!video.isEmpty()) {
                            try {
                                dialogCarregamento.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            DatabaseReference quizRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(idQuiz);

                            HashMap<String, Object> hashMapEditarQuiz = new HashMap<>();
                            hashMapEditarQuiz.put("titulo", tituloQuiz);

                            quizRef.updateChildren(hashMapEditarQuiz).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> hashMapEditarIntroQuiz = new HashMap<>();
                                    hashMapEditarIntroQuiz.put("titulo", tituloIntro);
                                    hashMapEditarIntroQuiz.put("descricao", descricao);
                                    hashMapEditarIntroQuiz.put("video", video);

                                    quizRef.child("introducao").updateChildren(hashMapEditarIntroQuiz).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            dialogCarregamento.dismiss();
                                            Toast.makeText(EditarQuizActivity.this, "Alterações guardadas com Sucesso!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            dialogCarregamento.dismiss();

                                            Toast.makeText(EditarQuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    dialogCarregamento.dismiss();
                                    Toast.makeText(EditarQuizActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(EditarQuizActivity.this, "Introduza um vídeo (Link do YouTube) para a Introdução!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditarQuizActivity.this, "Introduza uma Descrição para a Introdução!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarQuizActivity.this, "Introduza um Título para a Introdução!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditarQuizActivity.this, "Introduza um Título!", Toast.LENGTH_SHORT).show();
            }

        });

        buscarQuiz(idQuiz);

    }

    private void buscarQuiz(String idQuiz) {

        DatabaseReference quizRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(idQuiz);

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Quiz quiz = snapshot.getValue(Quiz.class);

                Glide.with(getApplicationContext()).load(Objects.requireNonNull(quiz).getImagem()).into(imageViewEditarQuiz);

                editTextTituloQuiz.setText(quiz.getTitulo());
                editTextTituloIntro.setText(quiz.getIntroducao().getTitulo());
                editTextDescricao.setText(quiz.getIntroducao().getDescricao());
                editTextVideo.setText(quiz.getIntroducao().getVideo());
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}