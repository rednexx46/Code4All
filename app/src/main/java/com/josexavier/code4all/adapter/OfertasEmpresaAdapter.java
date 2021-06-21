package com.josexavier.code4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.ContaActivity;
import com.josexavier.code4all.activity.EditarOfertaActivity;
import com.josexavier.code4all.activity.OfertaActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Oferta;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class OfertasEmpresaAdapter extends RecyclerView.Adapter<OfertasEmpresaAdapter.MyViewHolder> {

    private List<Oferta> listaOfertas;
    private Context context;

    public OfertasEmpresaAdapter(List<Oferta> ofertas, Context c) {
        this.listaOfertas = ofertas;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.ofertas_empresa_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Oferta oferta = listaOfertas.get(position);

        Glide.with(context).load(oferta.getFotoUtilizador()).into(holder.imagem);
        holder.nome.setText(oferta.getNomeUtilizador());
        holder.titulo.setText(oferta.getTitulo());
        holder.descricao.setText(oferta.getDescricao());
        holder.data.setText(oferta.getDataEnvio());

        // Abre a atividade necessária para visualizar a conta de um membro
        holder.imagem.setOnClickListener(v -> {
            Intent intentContaActivity = new Intent(context, ContaActivity.class);
            intentContaActivity.putExtra("idConta", oferta.getIdUtilizador());
            intentContaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intentContaActivity);
        });

        // Muda fundo do botão e texto consoante o seu estado
        if (oferta.getEstado().equals(Configs.ACEITE)) {
            Drawable drawableVerde = ContextCompat.getDrawable(context, R.drawable.retangulo_verde);
            holder.estado.setBackground(drawableVerde);
            holder.estado.setText(Configs.ACEITE);
        } else if (oferta.getEstado().equals(Configs.PENDENTE)) {
            Drawable drawableAmarelo = ContextCompat.getDrawable(context, R.drawable.retangulo_amarelo);
            holder.estado.setBackground(drawableAmarelo);
            holder.estado.setText(Configs.PENDENTE);
        } else if (oferta.getEstado().equals(Configs.RECUSADO)) {
            Drawable drawableVermelho = ContextCompat.getDrawable(context, R.drawable.retangulo_vermelho);
            holder.estado.setBackground(drawableVermelho);
            holder.estado.setText(Configs.RECUSADO);
        } else if (oferta.getEstado().equals(Configs.CONCLUIDO)) {
            Drawable drawableLaranja = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja);
            holder.estado.setBackground(drawableLaranja);
            holder.estado.setText(Configs.CONCLUIDO);
        }

        // Abre a atividade para observar a Oferta
        holder.estado.setOnClickListener(v -> {
            Intent intentOfertaEmpresaActivity = new Intent(context, OfertaActivity.class);
            intentOfertaEmpresaActivity.putExtra("idOferta", oferta.getId());
            intentOfertaEmpresaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intentOfertaEmpresaActivity);
        });

        holder.editar.setVisibility(View.GONE);
        holder.remover.setVisibility(View.GONE);

        if (listaOfertas.get(position).getEstado().equals(Configs.PENDENTE)) {
            holder.editar.setVisibility(View.VISIBLE);
            holder.remover.setVisibility(View.VISIBLE);
            holder.editar.setOnClickListener(v -> {
                Intent intentOfertaEmpresaActivity = new Intent(context, EditarOfertaActivity.class);
                intentOfertaEmpresaActivity.putExtra("idOferta", oferta.getId());
                intentOfertaEmpresaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intentOfertaEmpresaActivity);
            });
            holder.remover.setOnClickListener(v -> {
                android.app.AlertDialog dialogCarregamento = new SpotsDialog.Builder().setContext(context).setMessage("Removendo Oferta...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remover Oferta");
                builder.setCancelable(false);
                builder.setMessage("Tem a certeza, que pretende eliminar a Oferta \"" + listaOfertas.get(position).getTitulo() + "\" ?");
                builder.setPositiveButton("Sim", (dialog, which) -> {
                    dialogCarregamento.show();
                    DatabaseReference ofertasRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(listaOfertas.get(position).getId());
                    ofertasRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogCarregamento.dismiss();
                            dialog.dismiss();
                            Toast.makeText(context, "Oferta removida com Sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            dialogCarregamento.dismiss();
                            dialog.dismiss();
                        }
                    });
                });
                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return listaOfertas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imagem;
        private TextView nome, titulo, descricao, data;
        private Button estado, editar, remover;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // Configurações Iniciais
            imagem = itemView.findViewById(R.id.imageViewOfertasEmpresa);
            nome = itemView.findViewById(R.id.textViewNomeOfertasEmpresa);
            titulo = itemView.findViewById(R.id.textViewTituloOfertaOfertasEmpresa);
            descricao = itemView.findViewById(R.id.textViewDescricaoOfertasEmpresa);
            data = itemView.findViewById(R.id.textViewDataOfertasEmpresa);
            estado = itemView.findViewById(R.id.buttonEstadoOfertasEmpresa);
            editar = itemView.findViewById(R.id.buttonEditarOferta);
            remover = itemView.findViewById(R.id.buttonRemoverOferta);

        }

    }


}
