package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.EditarEmpresaActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Empresa;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class EmpresaFragment extends Fragment {

    private ImageView imageViewEmpresa;
    private LinearLayout linearLayoutEmpresa;
    private TextView textViewNome, textViewDescricao, textViewDescricaoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_empresa, container, false);


        // Configurações Iniciais
        imageViewEmpresa = root.findViewById(R.id.imageViewContaEmpresa);
        linearLayoutEmpresa = root.findViewById(R.id.linearLayoutEmpresa);
        textViewNome = root.findViewById(R.id.textViewEmpresaNome);
        textViewDescricao = root.findViewById(R.id.textViewEmpresaDescricao);
        textViewDescricaoData = root.findViewById(R.id.textEmpresaDescricaoData);
        Button buttonEditar;
        buttonEditar = root.findViewById(R.id.buttonEditarEmpresa);

        // Abrir EditarEmpresaActivity
        buttonEditar.setOnClickListener(v -> {
            Intent intentEditarEmpresa = new Intent(getContext(), EditarEmpresaActivity.class);
            intentEditarEmpresa.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intentEditarEmpresa);
        });

        recuperarDadosUtilizador();

        return root;

    }

    private void recuperarDadosUtilizador() { // Recuperar todos os dados da empresa
        DatabaseReference empresaRef = DefinicaoFirebase.recuperarBaseDados();
        String idUtilizador = Configs.recuperarIdUtilizador();

        empresaRef.child("contas").child(idUtilizador).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando dados...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Empresa empresa = snapshot.getValue(Empresa.class);
                Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.borda_preta);
                Objects.requireNonNull(drawable).setColorFilter(Objects.requireNonNull(empresa).getCorFundoPerfil(), PorterDuff.Mode.SRC);

                linearLayoutEmpresa.setBackground(drawable);
                Glide.with(requireContext()).load(empresa.getFoto()).into(imageViewEmpresa);
                textViewNome.setText(empresa.getNome());
                textViewDescricao.setText(empresa.getDescricao());
                textViewDescricaoData.setText("Última vez atualizado .: " + empresa.getDataDescricao());

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        recuperarDadosUtilizador();
    }
}