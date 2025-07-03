package com.example.receitasapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class DetalhesReceitaActivity extends AppCompatActivity {

    private TextView nomeText, descricaoText, preparoText;
    private ChipGroup chipGroupIngredientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_receita);

        // configura a Toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolbarDetalhesReceita);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // exibe o botão de voltar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nomeText              = findViewById(R.id.detalhesNome);
        descricaoText         = findViewById(R.id.detalhesDescricao);
        preparoText           = findViewById(R.id.detalhesPreparo);
        chipGroupIngredientes = findViewById(R.id.chipGroupIngredientes);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nomeText.setText(extras.getString("nome", ""));
            descricaoText.setText(extras.getString("descricao", ""));
            preparoText.setText(extras.getString("modoDePreparo", ""));

            String ingredientesCsv = extras.getString("ingredientes", "");
            if (!ingredientesCsv.isEmpty()) {
                String[] itens = ingredientesCsv.split(",");
                for (String ing : itens) {
                    Chip chip = new Chip(this);
                    chip.setText(ing.trim());
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chipGroupIngredientes.addView(chip);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // trata clique no botão de voltar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
