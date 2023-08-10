package com.example.testsecondapplication.api;

import com.example.testsecondapplication.modal.Motos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MotosApiClient {
    public static final String BASE_URL = "https://64d45dcdb592423e46940ccf.mockapi.io/api/";
    private static Retrofit retrofit;
    public MotosApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public void getMotosClient (Callback<List<Motos>> callback){
        MotosApi motosApi = retrofit.create(MotosApi.class);
        Call<List<Motos>> call = motosApi.getMotos();
        call.enqueue(callback);
    }

}
