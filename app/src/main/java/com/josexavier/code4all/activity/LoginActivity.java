package com.josexavier.code4all.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configuração dos campos de email e de password
        EditText editLoginEmail, editLoginPassword;
        editLoginEmail = findViewById(R.id.editTextEmailLogin);
        editLoginPassword = findViewById(R.id.editTextPasswordLogin);

        AlertDialog dialogAutenticacao = new SpotsDialog.Builder().setContext(this).setMessage("Autenticando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        // Configuração do Botão de Login
        Button buttonLogin;
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {

            // Recuperar o texto dos campos
            String email = editLoginEmail.getText().toString();
            String password = editLoginPassword.getText().toString();

            // Verificar o seu conteúdo
            if (!email.isEmpty()) {
                if (!password.isEmpty()) {
                    dialogAutenticacao.show();
                    FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();
                    autenticacao.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Começa a atividade consoante o grupo do utilizador
                            Configs.buscarUtilizador(getApplicationContext(), intent -> {
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                dialogAutenticacao.dismiss();
                                IntroActivity.activity.finish(); // Finaliza a IntroActivity
                                finish(); // Finaliza a LoginActivity
                                startActivity(intent); // Começa a nova atividade
                            });
                        } else if (!task.isSuccessful()) { // Tratamento de erros
                            String erro;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException excecao) {
                                erro = "Utilizador não registrado!";
                            } catch (FirebaseAuthInvalidCredentialsException excecao) {
                                erro = "Email e/ou Password não corretos!";
                            } catch (Exception excecao) {
                                erro = "Erro, tente novamente mais tarde!";
                                excecao.printStackTrace();
                            }
                            dialogAutenticacao.dismiss();
                            Toast.makeText(LoginActivity.this, erro, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Por favor, Introduza uma Password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Por favor, Introduza um Email!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void criarConta(View view) { // TextView CriarConta
        Intent atividadeLogin = new Intent(this, CriarContaActivity.class);
        startActivity(atividadeLogin);
        finish();
    }

    public void esquecerPassword(View view) { // TextView EsquecerPassword
        Intent atividadeEsquecerPassword = new Intent(this, EsquecerPasswordActivity.class);
        atividadeEsquecerPassword.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(atividadeEsquecerPassword);
    }

}