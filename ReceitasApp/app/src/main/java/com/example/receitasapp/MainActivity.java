package com.example.receitasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
;

import com.example.receitasapp.adapter.ReceitaAdapter;
import com.example.receitasapp.api.ReceitaApi;
import com.example.receitasapp.model.Receita;
import com.example.receitasapp.service.RetrofitService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReceitaAdapter receitaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Botão flutuante para adicionar nova receita
        FloatingActionButton addBtn = findViewById(R.id.addReceitaFab);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovaReceitaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarReceitas();
    }

    private void carregarReceitas() {
        ReceitaApi api = RetrofitService.getRetrofit().create(ReceitaApi.class);
        api.getReceitas().enqueue(new Callback<List<Receita>>() {
            @Override
            public void onResponse(Call<List<Receita>> call, Response<List<Receita>> response) {
                if (response.isSuccessful()) {
                    List<Receita> receitas = response.body();
                    receitaAdapter = new ReceitaAdapter(receitas, MainActivity.this, () -> carregarReceitas());

                    recyclerView.setAdapter(receitaAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao carregar receitas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Receita>> call, Throwable t) {
                Log.e("API_ERROR", "Erro ao conectar", t);
                Toast.makeText(MainActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}