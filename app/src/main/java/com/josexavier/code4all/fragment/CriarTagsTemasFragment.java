package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class CriarTagsTemasFragment extends Fragment {

    private EditText tema, tag;
    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_criar_tags_temas, container, false);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Criando TT...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        tema = root.findViewById(R.id.editTextCriarTemaTT);
        tag = root.findViewById(R.id.editTextCriarTagTT);

        Button criarTema, criarTag;
        criarTema = root.findViewById(R.id.buttonCriarTemaTT);
        criarTag = root.findViewById(R.id.buttonCriarTagTT);

        criarTema.setOnClickListener(v -> criarTT("tema", tema.getText().toString()));
        criarTag.setOnClickListener(v -> criarTT("tag", tag.getText().toString()));

        return root;

    }

    private void criarTT(String op, String texto) {
        if (!texto.isEmpty() || !texto.equals("")) { // se o campo está preenchido
            if (op.equals("tema")) { // op = tema
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DatabaseReference temasRef = DefinicaoFirebase.recuperarBaseDados().child("temas");

                temasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        int valorAtual = 0;

                        for (DataSnapshot dados : snapshot.getChildren())
                            valorAtual = Integer.parseInt(Objects.requireNonNull(dados.getKey())) + 1; // ir buscar ultimo ID

                        temasRef.child(String.valueOf(valorAtual)).setValue(texto).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                limparCampos();
                                Toast.makeText(getContext(), "Tema adicionado com Sucesso!", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            } else { // op = tag
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DatabaseReference tagsRef = DefinicaoFirebase.recuperarBaseDados().child("tags");
                tagsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        int valorAtual = 0;

                        for (DataSnapshot dados : snapshot.getChildren())
                            valorAtual = Integer.parseInt(Objects.requireNonNull(dados.getKey())) + 1; // ir buscar ultimo ID

                        tagsRef.child(String.valueOf(valorAtual)).setValue(texto).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                limparCampos();
                                Toast.makeText(getContext(), "Tag adicionado com Sucesso!", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        } else { // senão, informa o utilizador para preencher
            Toast.makeText(getContext(), "Introduza algo no campo desejado!", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCampos() {
        tag.setText("");
        tema.setText("");
    }

}