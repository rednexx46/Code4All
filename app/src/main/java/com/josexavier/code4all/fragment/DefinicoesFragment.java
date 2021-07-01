package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.InscricoesActivity;
import com.josexavier.code4all.activity.IntroActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class DefinicoesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_definicoes, container, false);

        // Configurações Iniciais
        Button buttonInscricoes, buttonRemoverConta, buttonAlterarPassword;
        buttonInscricoes = root.findViewById(R.id.buttonAlterarInscricoes);
        buttonAlterarPassword = root.findViewById(R.id.buttonAlterarPassword);
        buttonRemoverConta = root.findViewById(R.id.buttonRemoverConta);

        AlertDialog dialogGrupo = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        try {
            dialogGrupo.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Configs.recuperarGrupo(grupo -> {

            if (grupo.equals("membro")) {
                buttonInscricoes.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), InscricoesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                });
            } else
                buttonInscricoes.setVisibility(View.GONE);

            dialogGrupo.dismiss();

            AlertDialog dialogConta = new SpotsDialog.Builder().setContext(getContext()).setMessage("Removendo Conta...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();


            buttonRemoverConta.setOnClickListener(v -> {

                EditText editText = new EditText(getContext());
                Drawable bordaPreta = ContextCompat.getDrawable(requireContext(), R.drawable.borda_preta_quiz);
                Typeface face = ResourcesCompat.getFont(requireContext(), R.font.montserrat_medium);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(16, 16, 16, 16);
                editText.setBackground(bordaPreta);
                editText.setTypeface(face);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setLayoutParams(params);
                editText.setHint("Introduza a sua Password!");

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(editText);
                builder.setTitle("Remover Conta");
                builder.setMessage("Tem 100% de certeza que pretende remover a sua CONTA da Code4All?");
                builder.setPositiveButton("Sim", (dialog, which) -> {

                    String password = editText.getText().toString();

                    if (!password.isEmpty()) {
                        try {
                            dialogConta.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();
                        FirebaseUser utilizador = autenticacao.getCurrentUser();

                        Configs.recuperarEmailUtilizador(emailUtilizador -> {
                            AuthCredential credenciais = EmailAuthProvider
                                    .getCredential(emailUtilizador, password);

                            String idUtilizador = Objects.requireNonNull(utilizador).getUid();

                            utilizador.reauthenticate(credenciais).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DatabaseReference utilizadorRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(idUtilizador);
                                    utilizadorRef.removeValue().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            utilizador.delete().addOnCompleteListener(task3 -> {
                                                if (task3.isSuccessful()) {
                                                    dialogConta.dismiss();
                                                    dialog.dismiss();
                                                    autenticacao.signOut();
                                                    Toast.makeText(getContext(), "Conta removida com Sucesso!", Toast.LENGTH_SHORT).show();
                                                    Intent intentIntroActivity = new Intent(getContext(), IntroActivity.class);
                                                    intentIntroActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                    startActivity(intentIntroActivity);
                                                    requireActivity().finish();
                                                } else {
                                                    dialogConta.dismiss();
                                                    dialog.dismiss();
                                                    String erro;
                                                    try {
                                                        throw Objects.requireNonNull(task3.getException());
                                                    } catch (FirebaseAuthRecentLoginRequiredException e) {
                                                        erro = "Credenciais Expiradas!";
                                                    } catch (Exception e) {
                                                        erro = getString(R.string.erro);
                                                        e.printStackTrace();
                                                    }
                                                    Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            dialogConta.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    dialogConta.dismiss();
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                }
                            });

                        });
                    } else {
                        dialogConta.dismiss();
                        dialog.dismiss();
                        Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    }

                });
                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
                builder.show();
            });

            AlertDialog dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Alterando a Password...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

            buttonAlterarPassword.setOnClickListener(v -> {
                EditText editText = new EditText(getContext());
                Drawable bordaPreta = ContextCompat.getDrawable(requireContext(), R.drawable.borda_preta_quiz);
                Typeface face = ResourcesCompat.getFont(requireContext(), R.font.montserrat_medium);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(16, 16, 16, 16);
                editText.setBackground(bordaPreta);
                editText.setTypeface(face);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setLayoutParams(params);
                editText.setHint("Introduza uma Password!");

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Alterar Password");
                builder.setMessage("Para alterar a sua password, digite uma nova no campo abaixo!");
                builder.setView(editText);
                builder.setPositiveButton("Confirmar", (dialog, which) -> {
                    String password = editText.getText().toString();
                    if (!password.isEmpty() || !password.equals("")) {
                        try {
                            dialogCarregamento.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FirebaseAuth autenticacao = DefinicaoFirebase.recuperarAutenticacao();
                        FirebaseUser utilizador = autenticacao.getCurrentUser();
                        Objects.requireNonNull(utilizador).updatePassword(password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialogCarregamento.dismiss();
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Password atualizada com Sucesso!", Toast.LENGTH_SHORT).show();
                                autenticacao.signOut();
                                Intent intentIntroActivity = new Intent(getContext(), IntroActivity.class);
                                intentIntroActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intentIntroActivity);
                                requireActivity().finish();
                            } else {
                                String erro;
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    erro = "A Password introduzida é fraca!";
                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                    erro = "Credenciais Expiradas!";
                                } catch (Exception e) {
                                    erro = getString(R.string.erro);
                                    e.printStackTrace();
                                }
                                dialogCarregamento.dismiss();
                                dialog.dismiss();
                                Toast.makeText(getContext(), erro, Toast.LENGTH_SHORT).show();
                            }

                        });
                    } else {
                        Toast.makeText(getContext(), "Introduza a Password desejada antes de Confirmar!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
                builder.show();
            });

        });

        return root;
    }
}