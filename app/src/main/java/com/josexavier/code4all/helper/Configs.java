package com.josexavier.code4all.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.AdminActivity;
import com.josexavier.code4all.activity.EmpresaActivity;
import com.josexavier.code4all.activity.IntroActivity;
import com.josexavier.code4all.activity.ModeradorActivity;
import com.josexavier.code4all.activity.PrincipalActivity;
import com.josexavier.code4all.interfaces.RecuperarIntent;
import com.josexavier.code4all.interfaces.Utilizador;
import com.josexavier.code4all.interfaces.Validacao;
import com.josexavier.code4all.interfaces.VariavelTemp;
import com.josexavier.code4all.model.Conta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class Configs {

    public static final String emailCriador = "josexavier46@outlook.pt";
    public static final int SELECAO_GALERIA = 200;
    private static final String PREFS = "numero_execucoes";
    public static String[] grupos = {"membro", "mod", "admin", "empresa"};
    public static int cliques = 0;
    public static String PENDENTE = "pendente";
    public static String RECUSADO = "recusado";
    public static String ACEITE = "aceite";
    public static String CONCLUIDO = "concluido";

    public static void guardarNome(String nome, Validacao validacao) {

        UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        FirebaseUser utilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser();
        Objects.requireNonNull(utilizador).updateProfile(profileRequest).addOnCompleteListener(task -> validacao.isValidacaoSucesso(task.isSuccessful()));

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
        String idUtilizador = Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getUid();
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        utilizadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conta utilizador = snapshot.getValue(Conta.class);
                try {
                    String grupo = "";
                    if (Objects.requireNonNull(utilizador).getTipo() != null && !utilizador.getTipo().equals("")) {
                        grupo = utilizador.getTipo();
                    }
                    if (!grupo.equals("") && !grupo.isEmpty()) {
                        switch (grupo) {
                            case "membro": {
                                Intent intent = new Intent(context, PrincipalActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                recuperarIntent.recuperarIntent(intent);
                                break;
                            }
                            case "mod": {
                                Intent intent = new Intent(context, ModeradorActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                recuperarIntent.recuperarIntent(intent);
                                break;
                            }
                            case "admin": {
                                Intent intent = new Intent(context, AdminActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                recuperarIntent.recuperarIntent(intent);
                                break;
                            }
                            case "empresa": {
                                Intent intent = new Intent(context, EmpresaActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                recuperarIntent.recuperarIntent(intent);
                                break;
                            }
                            default:
                                Toast.makeText(context, context.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                recuperarIntent.recuperarIntent(null);
                                break;
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

    public static void recuperarGrupo(VariavelTemp grupo) {
        String idUtilizador = Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getUid();
        DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
        utilizadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conta utilizador = snapshot.getValue(Conta.class);
                if (Objects.requireNonNull(utilizador).getTipo() != null) {
                    grupo.tempVar(utilizador.getTipo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void recuperarUtilizador(Utilizador utilizador) {
        utilizador.recuperarUtilizador(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser());
    }

    public static String recuperarDataHoje() {
        SimpleDateFormat formatacaoData = new SimpleDateFormat("dd/MM/yy");
        return formatacaoData.format(new Date());
    }

    public static String recuperarIdUtilizador() {
        return Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getUid();
    }

    public static void recuperarIdUtilizador(VariavelTemp idUtilizador) {
        idUtilizador.tempVar(Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getUid());
    }

    public static String recuperarNomeUtilizador() {
        return Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getDisplayName();
    }

    public static void recuperarNomeUtilizador(VariavelTemp nomeUtilizador) {
        nomeUtilizador.tempVar(Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getDisplayName());
    }

    public static String recuperarEmailUtilizador() {
        return Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getEmail();
    }

    public static void recuperarEmailUtilizador(VariavelTemp emailUtilizador) {
        emailUtilizador.tempVar(Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getEmail());
    }

    public static String recuperarFotoUtilizador() {
        return Objects.requireNonNull(Objects.requireNonNull(DefinicaoFirebase.recuperarAutenticacao().getCurrentUser()).getPhotoUrl()).toString();
    }
}
