package com.josexavier.code4all.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.AdminActivity;
import com.josexavier.code4all.activity.EmpresaActivity;
import com.josexavier.code4all.activity.IntroActivity;
import com.josexavier.code4all.activity.ModeradorActivity;
import com.josexavier.code4all.activity.PrincipalActivity;
import com.josexavier.code4all.activity.RecuperarIntent;
import com.josexavier.code4all.activity.Validacao;
import com.josexavier.code4all.activity.VariavelTemp;
import com.josexavier.code4all.model.Conta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Configs {

    public static final String emailCriador = "josexavier46@outlook.pt";
    public static String[] grupos = {"membro", "mod", "admin", "empresa"};
    private static final String PREFS = "numero_execucoes";
    public static int cliques = 0;
    public static String PENDENTE = "pendente";
    public static String RECUSADO = "recusado";
    public static String ACEITE = "aceite";
    public static String CONCLUIDO = "concluido";
    public static final int SELECAO_CAMARA = 100;
    public static final int SELECAO_GALERIA = 200;

    public static void guardarNome(String nome, Validacao validacao) {

        UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        FirebaseUser utilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser();
        utilizador.updateProfile(profileRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                validacao.isValidacaoSucesso(true);
            else
                validacao.isValidacaoSucesso(false);
        });

    }

    public static void abrirGaleria(Activity atividade) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(atividade.getPackageManager()) != null) {
            atividade.startActivityForResult(i, SELECAO_GALERIA);
        }
    }

    public static void quantidadeVezesExecucao(Context context) {
        if (cliques == 5) {
            SharedPreferences settings = context.getSharedPreferences(PREFS, 0);
            int vezes = settings.getInt("vezes", 0);
            Toast.makeText(context, "Esta app, já foi executada " + vezes + " vezes!", Toast.LENGTH_SHORT).show();
            cliques = 0;
        }
        cliques++;
    }

    public static int corAleatoria() {

        Random aleatorio = new Random();
        int vermelho = aleatorio.nextInt(255);
        int verde = aleatorio.nextInt(255);
        int azul = aleatorio.nextInt(255);

        return Color.rgb(vermelho, verde, azul);

    }

    public static Bitmap desenharTexto(String text, int textWidth, int color) {
        // Obter as dimensões do Texto
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setTextSize(5000);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // Criação de um Bitmap e de um Canvas para o desenhar
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);

        // Desenhar Fundo no Bitmap
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawPaint(paint);

        // Desenhar Texto no Bitmap
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    public static void buscarUtilizador(Context context, RecuperarIntent recuperarIntent) {
        String idUtilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getUid();
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        utilizadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conta utilizador = snapshot.getValue(Conta.class);
                try {
                    String grupo = "";
                    if (utilizador.getTipo() != null && !utilizador.getTipo().equals("")) {
                        grupo = utilizador.getTipo();
                    }
                    if (grupo != "" && !grupo.isEmpty()) {
                        if (grupo.equals("membro")) {
                            Intent intent = new Intent(context, PrincipalActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            recuperarIntent.recuperarIntent(intent);
                        } else if (grupo.equals("mod")) {
                            Intent intent = new Intent(context, ModeradorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            recuperarIntent.recuperarIntent(intent);
                        } else if (grupo.equals("admin")) {
                            Intent intent = new Intent(context, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            recuperarIntent.recuperarIntent(intent);
                        } else if (grupo.equals("empresa")) {
                            Intent intent = new Intent(context, EmpresaActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            recuperarIntent.recuperarIntent(intent);
                        } else {
                            Toast.makeText(context, context.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            recuperarIntent.recuperarIntent(null);
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.erro), Toast.LENGTH_LONG).show();
                        recuperarIntent.recuperarIntent(null);
                    }
                } catch (Exception e) {
                    FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();
                    autenticacao.signOut();
                    Intent intent = new Intent(context, IntroActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    recuperarIntent.recuperarIntent(intent);
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static boolean verificarString(String string) {
        if (string != null && !string.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static void recuperarGrupo(VariavelTemp grupo) {
        String idUtilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getUid();
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        utilizadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conta utilizador = snapshot.getValue(Conta.class);
                if (utilizador.getTipo() != null) {
                    grupo.tempVar(utilizador.getTipo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static String recuperarDataHoje() {
        SimpleDateFormat formatacaoData = new SimpleDateFormat("dd/MM/yy");
        return formatacaoData.format(new Date());
    }

    public static String recuperarIdUtilizador() {
        return DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getUid();
    }

    public static void recuperarIdUtilizador(VariavelTemp idUtilizador) {
        idUtilizador.tempVar(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getUid());
    }

    public static String recuperarNomeUtilizador() {
        return DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getDisplayName();
    }

    public static void recuperarNomeUtilizador(VariavelTemp nomeUtilizador) {
        nomeUtilizador.tempVar(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getDisplayName());
    }

    public static String recuperarEmailUtilizador() {
        return DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getEmail();
    }

    public static void recuperarEmailUtilizador(VariavelTemp emailUtilizador) {
        emailUtilizador.tempVar(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getEmail());
    }

    public static String recuperarFotoUtilizador() {
        return DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getPhotoUrl().toString();
    }
}
