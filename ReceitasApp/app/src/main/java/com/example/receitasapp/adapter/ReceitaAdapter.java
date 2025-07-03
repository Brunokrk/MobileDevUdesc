package com.example.receitasapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.receitasapp.DetalhesReceitaActivity;
import com.example.receitasapp.NovaReceitaActivity;
import com.example.receitasapp.R;
import com.example.receitasapp.api.ReceitaApi;
import com.example.receitasapp.model.Receita;
import com.example.receitasapp.service.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceitaAdapter extends RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder> {

    private List<Receita> receitas;
    private Context context;
    private Runnable onReceitaDeletada;

    public ReceitaAdapter(List<Receita> receitas, Context context, Runnable onReceitaDeletada) {
        this.receitas = receitas;
        this.context = context;
        this.onReceitaDeletada = onReceitaDeletada;
    }

    @Override
    public ReceitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receita, parent, false);
        return new ReceitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReceitaViewHolder holder, int position) {
        Receita receita = receitas.get(position);
        holder.nomeTextView.setText(receita.getNome());
        holder.descricaoTextView.setText(receita.getDescricao());

        // clique para visualizar detalhes
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesReceitaActivity.class);
            intent.putExtra("nome", receita.getNome());
            intent.putExtra("descricao", receita.getDescricao());
            intent.putExtra("ingredientes", String.join(",", receita.getIngredientes()));
            intent.putExtra("modoDePreparo", receita.getModoDePreparo());
            context.startActivity(intent);
        });

        // clique para editar
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, NovaReceitaActivity.class);
            intent.putExtra("id", receita.getId());
            intent.putExtra("nome", receita.getNome());
            intent.putExtra("descricao", receita.getDescricao());
            intent.putExtra("ingredientes", String.join(",", receita.getIngredientes()));
            intent.putExtra("modoDePreparo", receita.getModoDePreparo());
            context.startActivity(intent);
        });


        // clique para deletar
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar exclusão")
                    .setMessage("Deseja realmente excluir a receita \"" + receita.getNome() + "\"?")
                    .setPositiveButton("Sim", (dialog, which) -> deletarReceita(receita.getId()))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void deletarReceita(Long id) {
        ReceitaApi api = RetrofitService.getRetrofit().create(ReceitaApi.class);
        api.deletarReceita(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                onReceitaDeletada.run();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                new AlertDialog.Builder(context)
                        .setTitle("Erro")
                        .setMessage("Erro ao deletar: " + t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return receitas.size();
    }

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView, descricaoTextView;
        ImageButton editButton, deleteButton;

        public ReceitaViewHolder(View itemView) {
            super(itemView);
            nomeTextView      = itemView.findViewById(R.id.nomeTextView);
            descricaoTextView = itemView.findViewById(R.id.descricaoTextView);
            editButton        = itemView.findViewById(R.id.editButton);
            deleteButton      = itemView.findViewById(R.id.deleteButton);
        }
    }
}
