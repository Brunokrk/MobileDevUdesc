package com.example.receitasapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.receitasapp.api.ReceitaApi;
import com.example.receitasapp.model.Receita;
import com.example.receitasapp.service.RetrofitService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovaReceitaActivity extends AppCompatActivity {

    private TextInputEditText nomeInput, descricaoInput, ingredienteInput, preparoInput;
    private MaterialButton adicionarIngredienteBtn, salvarBtn;
    private ChipGroup chipGroupIngredientes;
    private MaterialToolbar toolbar;

    private List<String> ingredientes = new ArrayList<>();
    private Long receitaId = null;
    private boolean isEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_receita);

        // Toolbar
        toolbar = findViewById(R.id.toolbarNovaReceita);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inputs
        nomeInput = findViewById(R.id.nomeInput);
        descricaoInput = findViewById(R.id.descricaoInput);
        ingredienteInput = findViewById(R.id.ingredienteInput);
        preparoInput = findViewById(R.id.preparoInput);
        adicionarIngredienteBtn = findViewById(R.id.adicionarIngredienteBtn);
        salvarBtn = findViewById(R.id.salvarBtn);
        chipGroupIngredientes = findViewById(R.id.chipGroupIngredientes);

        // Botão adicionar ingrediente
        adicionarIngredienteBtn.setOnClickListener(v -> {
            String novo = ingredienteInput.getText().toString().trim();
            if (!novo.isEmpty()) {
                adicionarChip(novo);
                ingredienteInput.setText("");
            } else {
                Toast.makeText(this, "Digite um ingrediente", Toast.LENGTH_SHORT).show();
            }
        });

        // Edição
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("id")) {
            isEdicao = true;
            receitaId = extras.getLong("id", -1);
            nomeInput.setText(extras.getString("nome", ""));
            descricaoInput.setText(extras.getString("descricao", ""));
            preparoInput.setText(extras.getString("modoDePreparo", ""));

            String csv = extras.getString("ingredientes", "");
            if (!csv.isEmpty()) {
                Arrays.stream(csv.split(","))
                        .forEach(this::adicionarChip);
            }

            getSupportActionBar().setTitle("Editar Receita");
        }

        // Salvar
        salvarBtn.setOnClickListener(v -> salvarReceita());
    }

    private void adicionarChip(String texto) {
        ingredientes.add(texto);
        Chip chip = new Chip(this);
        chip.setText(texto);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            ingredientes.remove(texto);
            chipGroupIngredientes.removeView(chip);
        });
        chipGroupIngredientes.addView(chip);
    }

    private void salvarReceita() {
        Receita receita = new Receita();
        receita.setNome(nomeInput.getText().toString());
        receita.setDescricao(descricaoInput.getText().toString());
        receita.setIngredientes(ingredientes);
        receita.setModoDePreparo(preparoInput.getText().toString());

        ReceitaApi api = RetrofitService.getRetrofit().create(ReceitaApi.class);
        Call<Receita> call = isEdicao
                ? api.atualizarReceita(receitaId, receita)
                : api.salvarReceita(receita);

        call.enqueue(new Callback<Receita>() {
            @Override
            public void onResponse(Call<Receita> call, Response<Receita> response) {
                if (response.isSuccessful()) {
                    String msg = isEdicao ? "Receita atualizada!" : "Receita salva!";
                    Toast.makeText(NovaReceitaActivity.this, msg, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NovaReceitaActivity.this, "Erro HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Receita> call, Throwable t) {
                Toast.makeText(NovaReceitaActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
