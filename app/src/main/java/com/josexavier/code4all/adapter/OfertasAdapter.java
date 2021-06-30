package com.josexavier.code4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.EmpresaVisualizacaoActivity;
import com.josexavier.code4all.activity.OfertaActivity;
import com.josexavier.code4all.model.Oferta;

import java.util.List;

public class OfertasAdapter extends RecyclerView.Adapter<OfertasAdapter.MyViewHolder> {

    private List<Oferta> listaOfertas;
    private Context context;

    public OfertasAdapter(List<Oferta> lista, Context c) {
        this.listaOfertas = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.ofertas_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Oferta oferta = listaOfertas.get(position);

        Glide.with(context).load(oferta.getFotoEmpresa()).into(holder.imagem);
        holder.empresa.setText(oferta.getNomeEmpresa());
        holder.descricao.setText(oferta.descricao);
        holder.estado.setText(oferta.getEstado());

        holder.imagem.setOnClickListener(v -> {
            Intent intentOfertaEmpresaActivity = new Intent(context, EmpresaVisualizacaoActivity.class);
            intentOfertaEmpresaActivity.putExtra("idEmpresa", oferta.getIdEmpresa());
            intentOfertaEmpresaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intentOfertaEmpresaActivity);
        });

        holder.buttonVisualizarOferta.setOnClickListener(v -> {
            Intent intentOfertaEmpresaActivity = new Intent(context, OfertaActivity.class);
            intentOfertaEmpresaActivity.putExtra("idOferta", oferta.getId());
            intentOfertaEmpresaActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intentOfertaEmpresaActivity);
        });

    }

    @Override
    public int getItemCount() {
        return listaOfertas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView empresa, descricao, estado;
        private Button buttonVisualizarOferta;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imageOfertasEmpresa);
            empresa = itemView.findViewById(R.id.textOfertasNomeEmpresa);
            descricao = itemView.findViewById(R.id.textOfertasDescricao);
            estado = itemView.findViewById(R.id.textOfertasEstado);
            buttonVisualizarOferta = itemView.findViewById(R.id.buttonVisualizarOferta);
        }
    }

}
