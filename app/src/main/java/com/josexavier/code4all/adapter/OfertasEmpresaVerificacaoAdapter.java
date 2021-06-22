package com.josexavier.code4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.ContaActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Oferta;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfertasEmpresaVerificacaoAdapter extends RecyclerView.Adapter<OfertasEmpresaVerificacaoAdapter.MyViewHolder> {

    private List<Oferta> listaOfertas;
    private Context context;

    public OfertasEmpresaVerificacaoAdapter(List<Oferta> ofertas, Context c) {
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
        holder.imagem.setOnClickListener(v -> {
            Intent intentContaActivity = new Intent(context, ContaActivity.class);
            intentContaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intentContaActivity.putExtra("idConta", oferta.getIdUtilizador());
            context.startActivity(intentContaActivity);
        });

        // Configuração do botão para que este abra a app de email, com os dados da oferta :)
        Drawable drawableLaranja = ContextCompat.getDrawable(context, R.drawable.retangulo_laranja);
        holder.estado.setBackground(drawableLaranja);
        holder.estado.setText("Enviar Email");
        holder.estado.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("message/rfc822");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{oferta.getEmailUtilizador()});
            intent.putExtra(Intent.EXTRA_SUBJECT, oferta.getTitulo() + " - Code4All");
            intent.putExtra(Intent.EXTRA_TEXT, "Boas,\n\n\n\nCumprimentos,\n\n" + oferta.getNomeEmpresa());

            try {
                DatabaseReference ofertaRef = DefinicaoFirebase.recuperarBaseDados().child("ofertas").child(oferta.getId());
                HashMap<String, Object> hashMapEstado = new HashMap<>();
                hashMapEstado.put("estado", Configs.CONCLUIDO);
                ofertaRef.updateChildren(hashMapEstado);
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "Sem apps de email instaladas.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaOfertas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imagem;
        private TextView nome, titulo, descricao, data;
        private Button estado;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // Configurações Iniciais
            imagem = itemView.findViewById(R.id.imageViewOfertasEmpresa);
            nome = itemView.findViewById(R.id.textViewNomeOfertasEmpresa);
            titulo = itemView.findViewById(R.id.textViewTituloOfertaOfertasEmpresa);
            descricao = itemView.findViewById(R.id.textViewDescricaoOfertasEmpresa);
            data = itemView.findViewById(R.id.textViewDataOfertasEmpresa);
            estado = itemView.findViewById(R.id.buttonEstadoOfertasEmpresa);

        }
    }

}
