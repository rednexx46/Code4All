package com.josexavier.code4all.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.EmpresaVisualizacaoActivity;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.model.Empresa;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmpresasAdapter extends RecyclerView.Adapter<EmpresasAdapter.MyViewHolder> {

    private List<Empresa> listaEmpresas;
    private Context context;

    public EmpresasAdapter(List<Empresa> empresas, Context c) {
        this.listaEmpresas = empresas;
        this.context = c;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.empresas_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Empresa empresa = listaEmpresas.get(position);

        Glide.with(context).load(empresa.getFoto()).into(holder.imagem);
        holder.nome.setText(empresa.getNome());
        holder.localidade.setText(empresa.getLocalidade());
        holder.empregados.setText(empresa.getEmpregados());
        holder.nif.setText("NIF .: " + empresa.getNif());
        holder.imagem.setOnClickListener(v -> {
            Intent intentEmpresaVisualizacao = new Intent(context, EmpresaVisualizacaoActivity.class);
            intentEmpresaVisualizacao.putExtra("idEmpresa", empresa.getId());
            context.startActivity(intentEmpresaVisualizacao);
        });
        holder.email.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("message/rfc822");
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{empresa.getEmail()});
            Configs.recuperarNomeUtilizador(nomeUtilizador -> {
                intent.putExtra(Intent.EXTRA_SUBJECT, "Informação da Empresa \"" + Configs.recuperarNomeUtilizador() + "\" - Code4All");
                intent.putExtra(Intent.EXTRA_TEXT, "Boas,\n\n\n\nCumprimentos,\n\n" + Configs.recuperarNomeUtilizador());
                try {
                    context.startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "Sem apps de email instaladas.", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return listaEmpresas.size();
    }

    public void filtrarDados(List<Empresa> listaFiltrada) {
        listaEmpresas = listaFiltrada;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView nome, localidade, empregados, nif;
        private Button email;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewEmpresas);
            nome = itemView.findViewById(R.id.textViewNomeEmpresas);
            localidade = itemView.findViewById(R.id.textViewLocalidadeEmpresas);
            empregados = itemView.findViewById(R.id.textViewEmpregadosEmpresas);
            nif = itemView.findViewById(R.id.textViewNIFEmpresas);
            email = itemView.findViewById(R.id.buttonEnviarEmailEmpresa);

        }
    }

}
