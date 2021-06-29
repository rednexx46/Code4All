package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import dmax.dialog.SpotsDialog;

public class EsquecerPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esquecer_password);

        editTextEmail = findViewById(R.id.editTextEmailEsquecerPassword);

    }

    public void pedirReposicaoPassword(View view) {

        String email = editTextEmail.getText().toString();

        dialog = new SpotsDialog.Builder().setContext(EsquecerPasswordActivity.this).setMessage("Carregando...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        if (!email.isEmpty()) {
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();

            autenticacao.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(EsquecerPasswordActivity.this, "Reposição de password enviada com Sucesso para o email \"" + email + "\"", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(EsquecerPasswordActivity.this, getString(R.string.erro), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Introduza um Email!", Toast.LENGTH_SHORT).show();
        }

    }
}