package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ListarTagsTemasFragment extends Fragment {

    private ArrayList<String> listaTemas = new ArrayList<>();
    private ArrayList<String> idTemas = new ArrayList<>();
    private ArrayAdapter<String> listaTemasAdapter;

    private ArrayList<String> listaTags = new ArrayList<>();
    private ArrayList<String> idTags = new ArrayList<>();
    private ArrayAdapter<String> listaTagsAdapter;
    private AlertDialog dialogRemocao, dialogCarregamento;

    private boolean isTemaVisivel = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_listar_tags_temas, container, false);

        dialogRemocao = new SpotsDialog.Builder().setContext(getContext()).setMessage("Removendo TT...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
        dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        ListView temas, tags;

        temas = root.findViewById(R.id.listViewTema);
        listaTemasAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, listaTemas);
        temas.setAdapter(listaTemasAdapter);
        temas.setOnItemClickListener((parent, view, position, id) -> {

            EditText editText = new EditText(getContext());
            Drawable bordaPreta = ContextCompat.getDrawable(getContext(), R.drawable.borda_preta_quiz);
            Typeface face = ResourcesCompat.getFont(getContext(), R.font.montserrat_medium);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(16, 16, 16, 16);
            editText.setBackground(bordaPreta);
            editText.setTypeface(face);
            editText.setLayoutParams(params);
            editText.setHint("Introduza um Tema!");
            editText.setText(listaTemas.get(position));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Editar Tema");
            builder.setMessage("Está atualmente a editar o Tema " + idTemas.get(position));
            builder.setView(editText);
            builder.setPositiveButton("Confirmar", (dialog, which) -> {
                String tema = editText.getText().toString();
                if (!tema.isEmpty() || !tema.equals("")) {
                    dialogRemocao.show();
                    DatabaseReference temaEditar = DefinicaoFirebase.recuperarBaseDados().child("temas").child(idTemas.get(position));
                    temaEditar.setValue(tema).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Tema atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                            listaTemasAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            dialogRemocao.dismiss();
                        } else {
                            dialogRemocao.dismiss();
                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Introduza algo antes de Confirmar!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        temas.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Remover Tema");
            builder.setCancelable(false);
            builder.setMessage("Tem a certeza, que pretende eliminar o Tema \"" + listaTemas.get(position) + "\" ?");
            builder.setPositiveButton("Sim", (dialog, which) -> {

                DatabaseReference tagsRef = DefinicaoFirebase.recuperarBaseDados().child("temas");
                tagsRef.child(idTemas.get(position)).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Tema Eliminado com Sucesso!", Toast.LENGTH_SHORT).show();
                        listaTemasAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });

        tags = root.findViewById(R.id.listViewTag);
        listaTagsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, listaTags);
        tags.setAdapter(listaTagsAdapter);
        tags.setOnItemClickListener((parent, view, position, id) -> {

            EditText editText = new EditText(getContext());
            Drawable bordaPreta = ContextCompat.getDrawable(getContext(), R.drawable.borda_preta_quiz);
            Typeface face = ResourcesCompat.getFont(getContext(), R.font.montserrat_medium);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(16, 16, 16, 16);
            editText.setBackground(bordaPreta);
            editText.setTypeface(face);
            editText.setLayoutParams(params);
            editText.setHint("Introduza uma Tag!");
            editText.setText(listaTags.get(position));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Editar Tag");
            builder.setMessage("Está atualmente a editar a Tag " + idTags.get(position));
            builder.setView(editText);
            builder.setPositiveButton("Confirmar", (dialog, which) -> {
                String tagAtualizada = editText.getText().toString();
                if (!tagAtualizada.isEmpty() || !tagAtualizada.equals("")) {
                    DatabaseReference tagEditar = DefinicaoFirebase.recuperarBaseDados().child("tags").child(idTags.get(position));
                    tagEditar.setValue(tagAtualizada).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Tag atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                            listaTagsAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Introduza algo antes de Confirmar!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        tags.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Remover Tag");
            builder.setCancelable(false);
            builder.setMessage("Tem a certeza, que pretende eliminar a Tag \"" + listaTags.get(position) + "\" ?");
            builder.setPositiveButton("Sim", (dialog, which) -> {
                DatabaseReference tagsRef = DefinicaoFirebase.recuperarBaseDados().child("tags");
                tagsRef.child(idTags.get(position)).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Tag Eliminada com Sucesso!", Toast.LENGTH_SHORT).show();
                        listaTagsAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });

        buscarTemas();
        buscarTags();

        trocarTT(root);

        return root;

    }

    private void trocarTT(View root) {
        FloatingActionButton fab;
        fab = root.findViewById(R.id.fabTrocarTT);

        LinearLayout linearLayoutTag, linearLayoutTema;
        linearLayoutTag = root.findViewById(R.id.linearLayoutListarTag);
        linearLayoutTema = root.findViewById(R.id.linearLayoutListarTema);

        fab.setOnClickListener(v -> {

            if (isTemaVisivel) {
                linearLayoutTag.setVisibility(View.VISIBLE);
                linearLayoutTema.setVisibility(View.GONE);
                isTemaVisivel = false;
            } else {
                linearLayoutTag.setVisibility(View.GONE);
                linearLayoutTema.setVisibility(View.VISIBLE);
                isTemaVisivel = true;
            }
        });
    }

    private void buscarTemas() {
        dialogCarregamento.show();
        DatabaseReference temasRef = DefinicaoFirebase.recuperarBaseDados().child("temas");

        temasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTemas.clear();
                idTemas.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    idTemas.add(dados.getKey());
                    listaTemas.add(dados.getValue(String.class));
                }
                dialogCarregamento.dismiss();
                listaTemasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void buscarTags() {
        dialogCarregamento.show();
        DatabaseReference temasRef = DefinicaoFirebase.recuperarBaseDados().child("tags");

        temasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listaTags.clear();
                idTags.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    idTags.add(dados.getKey());
                    listaTags.add(dados.getValue(String.class));
                }
                dialogCarregamento.dismiss();
                listaTagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}