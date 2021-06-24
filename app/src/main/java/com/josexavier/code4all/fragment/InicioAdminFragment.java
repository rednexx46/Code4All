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

public class InicioAdminFragment extends Fragment {

    private TextView textPostsFazer, textMembros, textEmpresas, textMods, textAdmins, textQuizes, textPosts, textOfertas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inicio_admin, container, false);

        textPostsFazer = root.findViewById(R.id.textCountAdminPostsFazer);

        textMembros = root.findViewById(R.id.textCountAdminMembros);
        textEmpresas = root.findViewById(R.id.textCountAdminEmpresas);
        textMods = root.findViewById(R.id.textCountAdminMods);
        textAdmins = root.findViewById(R.id.textCountAdminAdmins);
        textQuizes = root.findViewById(R.id.textCountAdminQuizes);
        textPosts = root.findViewById(R.id.textCountAdminPosts);
        textOfertas = root.findViewById(R.id.textCountAdminOfertas);

        buscarInfo();

        return root;

    }

    private void buscarInfo() {
        DatabaseReference referenciaGeral = DefinicaoFirebase.recuperarBaseDados();

        referenciaGeral.child("contas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                int membros = 0, empresas = 0, mods = 0, admins = 0;

                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (dados.getValue(Conta.class).getTipo() != null) {
                        if (dados.getValue(Conta.class).getTipo().equals("membro")) {
                            membros++;
                        } else if (dados.getValue(Conta.class).getTipo().equals("empresa")) {
                            empresas++;
                        } else if (dados.getValue(Conta.class).getTipo().equals("mod")) {
                            mods++;
                        } else if (dados.getValue(Conta.class).getTipo().equals("admin")) {
                            admins++;
                        }
                    }
                }

                textMembros.setText(String.valueOf(membros));
                textEmpresas.setText(String.valueOf(empresas));
                textMods.setText(String.valueOf(mods));
                textAdmins.setText(String.valueOf(admins));

                referenciaGeral.child("quizes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        int quizes = (int) snapshot.getChildrenCount();
                        textQuizes.setText(String.valueOf(quizes));

                        referenciaGeral.child("posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                int posts = 0, postsFazer = 0;

                                for (DataSnapshot dados : snapshot.getChildren()) {
                                    if (dados.getValue(Post.class).getEstado().equals(Configs.ACEITE)) {
                                        posts++;
                                    } else if (dados.getValue(Post.class).getEstado().equals(Configs.PENDENTE)) {
                                        postsFazer++;
                                    }
                                }

                                textPosts.setText(String.valueOf(posts));
                                textPostsFazer.setText(String.valueOf(postsFazer));

                                referenciaGeral.child("ofertas").addValueEventListener(new ValueEventListener() {
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

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}