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
import com.josexavier.code4all.model.Oferta;

import org.jetbrains.annotations.NotNull;

import dmax.dialog.SpotsDialog;

public class InicioEmpresaFragment extends Fragment {

    private TextView textViewPendentes, textViewTotal, textViewConcluidas, textViewAceites, textViewRecusadas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inicio_empresa, container, false);

        // Configurações Iniciais
        textViewPendentes = root.findViewById(R.id.textCountOfertasPendentesEmpresa);
        textViewTotal = root.findViewById(R.id.textCountOfertasTotalEmpresa);
        textViewConcluidas = root.findViewById(R.id.textCountOfertasConcluidasEmpresa);
        textViewAceites = root.findViewById(R.id.textCountOfertasAceitesEmpresa);
        textViewRecusadas = root.findViewById(R.id.textCountOfertasRecusadasEmpresa);

        String idUtilizador = Configs.recuperarIdUtilizador();

        buscarInfo(idUtilizador);

        return root;

    }

    private void buscarInfo(String idUtilizador) { // Buscar todas as informações da empresa em geral
        DatabaseReference ofertasRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas");
        ofertasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                AlertDialog dialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Carregando Ofertas...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
                dialog.show();


                int concluidas = 0, aceites = 0, recusadas = 0, pendentes = 0;

                for (DataSnapshot dados : snapshot.getChildren()) {
                    String estado = dados.getValue(Oferta.class).getEstado();

                    if (dados.getValue(Oferta.class).getIdEmpresa().equals(idUtilizador)) {
                        if (estado.equals(Configs.CONCLUIDO))
                            concluidas++;
                        else if (estado.equals(Configs.PENDENTE))
                            pendentes++;
                        else if (estado.equals(Configs.ACEITE))
                            aceites++;
                        else if (estado.equals(Configs.RECUSADO))
                            recusadas++;
                    }

                }

                int total = concluidas + aceites + recusadas + pendentes;

                textViewConcluidas.setText(String.valueOf(concluidas));
                textViewPendentes.setText(String.valueOf(pendentes));
                textViewAceites.setText(String.valueOf(aceites));
                textViewRecusadas.setText(String.valueOf(recusadas));
                textViewTotal.setText(String.valueOf(total));

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}