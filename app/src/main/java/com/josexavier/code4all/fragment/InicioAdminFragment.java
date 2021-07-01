package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
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

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class InicioAdminFragment extends Fragment {

    private TextView textPostsFazer, textMembros, textEmpresas, textMods, textAdmins, textQuizes, textPosts, textOfertas;
    private DatabaseReference geralRef;
    private ValueEventListener geralEventListener;

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
        geralRef = DefinicaoFirebase.recuperarBaseDados();
        AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        geralEventListener = geralRef.child("contas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int membros = 0, empresas = 0, mods = 0, admins = 0;

                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (Objects.requireNonNull(dados.getValue(Conta.class)).getTipo() != null) {
                        switch (Objects.requireNonNull(dados.getValue(Conta.class)).getTipo()) {
                            case "membro":
                                membros++;
                                break;
                            case "empresa":
                                empresas++;
                                break;
                            case "mod":
                                mods++;
                                break;
                            case "admin":
                                admins++;
                                break;
                        }
                    }
                }

                textMembros.setText(String.valueOf(membros));
                textEmpresas.setText(String.valueOf(empresas));
                textMods.setText(String.valueOf(mods));
                textAdmins.setText(String.valueOf(admins));

                geralRef.child("quizes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        int quizes = (int) snapshot.getChildrenCount();
                        textQuizes.setText(String.valueOf(quizes));

                        geralRef.child("posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                int posts = 0, postsFazer = 0;

                                for (DataSnapshot dados : snapshot.getChildren()) {
                                    if (Objects.requireNonNull(dados.getValue(Post.class)).getEstado().equals(Configs.ACEITE)) {
                                        posts++;
                                    } else if (Objects.requireNonNull(dados.getValue(Post.class)).getEstado().equals(Configs.PENDENTE)) {
                                        postsFazer++;
                                    }
                                }

                                textPosts.setText(String.valueOf(posts));
                                textPostsFazer.setText(String.valueOf(postsFazer));

                                geralRef.child("ofertas").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                        int ofertas = (int) snapshot.getChildrenCount();
                                        textOfertas.setText(String.valueOf(ofertas));
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
    public void onDestroy() {
        super.onDestroy();
        geralRef.removeEventListener(geralEventListener);
    }
}