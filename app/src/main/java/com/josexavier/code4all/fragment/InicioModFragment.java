package com.josexavier.code4all.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Conta;
import com.josexavier.code4all.model.Post;

import org.jetbrains.annotations.NotNull;

public class InicioModFragment extends Fragment {

    private TextView textPostsFazer, textMembros, textEmpresas, textModeradores, textPosts, textOfertas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inicio_mod, container, false);

        textPostsFazer = root.findViewById(R.id.textCountModPostsFazer);
        textMembros = root.findViewById(R.id.textCountModMembros);
        textEmpresas = root.findViewById(R.id.textCountModEmpresas);
        textModeradores = root.findViewById(R.id.textCountModModeradores);
        textPosts = root.findViewById(R.id.textCountModPosts);
        textOfertas = root.findViewById(R.id.textCountModOfertas);

        buscarInfo();

        return root;

    }

    private void buscarInfo() {
        DatabaseReference ref = DefinicaoFirebase.recuperarBaseDados();

        ref.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int postsFazer = 0, posts = 0;

                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (dados.getValue(Post.class).getEstado().equals(Configs.PENDENTE)) {
                        postsFazer++;
                    } else if (dados.getValue(Post.class).getEstado().equals(Configs.ACEITE)) {
                        posts++;
                    }
                }

                textPostsFazer.setText(String.valueOf(postsFazer));
                textPosts.setText(String.valueOf(posts));

                ref.child("contas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        int membros = 0, empresas = 0, mods = 0;

                        for (DataSnapshot dados : snapshot.getChildren()) {
                            if (dados.getValue(Conta.class).getTipo() != null) {
                                if (dados.getValue(Conta.class).getTipo().equals("membro")) {
                                    membros++;
                                } else if (dados.getValue(Conta.class).getTipo().equals("empresa")) {
                                    empresas++;
                                } else if (dados.getValue(Conta.class).getTipo().equals("mod")) {
                                    mods++;
                                }
                            }
                        }

                        textMembros.setText(String.valueOf(membros));
                        textEmpresas.setText(String.valueOf(empresas));
                        textModeradores.setText(String.valueOf(mods));

                        ref.child("ofertas").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                int ofertas = (int) snapshot.getChildrenCount();
                                textOfertas.setText(String.valueOf(ofertas));
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

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}