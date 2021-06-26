package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.interfaces.Validacao;
import com.josexavier.code4all.model.Introducao;
import com.josexavier.code4all.model.Quiz;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class QuizInfoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String GOOGLE_API_KEY = "AIzaSyCHNyYONXCWN6hKyBgMMTfd4tpQYptXePQ";
    private YouTubePlayerView youtubePlayerView;
    private Introducao introducao;
    private ImageView imagemQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_info);

        imagemQuiz = findViewById(R.id.imageViewQuizInfo);
        TextView textViewTitulo = findViewById(R.id.textViewTituloQuizInfo);
        TextView textViewDescricao = findViewById(R.id.textViewDescricaoQuizInfo);

        String idQuiz = getIntent().getStringExtra("idQuiz");
        buscarQuiz(idQuiz, validar -> {
            if (validar) {
                textViewDescricao.setText(introducao.getDescricao());
                textViewTitulo.setText(introducao.getTitulo());
                youtubePlayerView = findViewById(R.id.viewYoutubePlayer);
                try {
                    youtubePlayerView.initialize(GOOGLE_API_KEY, QuizInfoActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void buscarQuiz(String idQuiz, Validacao validacao) {
        AlertDialog dialog = new SpotsDialog.Builder().setContext(QuizInfoActivity.this).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        DatabaseReference quizRef = DefinicaoFirebase.recuperarBaseDados().child("quizes").child(idQuiz);
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialog.show();
                String linkImagem = snapshot.getValue(Quiz.class).getImagem();

                Glide.with(getApplicationContext()).load(linkImagem).into(imagemQuiz);

                quizRef.child("introducao").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        introducao = snapshot.getValue(Introducao.class);
                        validacao.isValidacaoSucesso(true);
                        dialog.dismiss();
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

    }

    private String obterIDYoutube(String linkYoutube) {
        String padrao = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern padraoCompilado = Pattern.compile(padrao);
        Matcher matcher = padraoCompilado.matcher(linkYoutube);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "erro";
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean foiRestaurado) {

        youTubePlayer.setShowFullscreenButton(true);

        if (!foiRestaurado) {

            String idVideo = obterIDYoutube(introducao.getVideo());
            if (!idVideo.equals("erro"))
                youTubePlayer.cueVideo(obterIDYoutube(introducao.getVideo()));
            else
                Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Erro ao iniciar Player!", Toast.LENGTH_SHORT).show();
    }

}