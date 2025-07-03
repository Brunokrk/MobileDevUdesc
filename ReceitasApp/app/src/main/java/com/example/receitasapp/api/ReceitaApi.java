package com.example.receitasapp.api;

import com.example.receitasapp.model.Receita;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ReceitaApi {
    @GET("/api/receitas")
    Call<List<Receita>> getReceitas();

    @POST("/api/receitas")
    Call<Receita> salvarReceita(@Body Receita receita);

    @DELETE("/api/receitas/{id}")
    Call<Void> deletarReceita(@Path("id") Long id);

    @PUT("/api/receitas/{id}")
    Call<Receita> atualizarReceita(@Path("id") Long id, @Body Receita receita);
}
