package com.example.testsecondapplication.api;

import com.example.testsecondapplication.modal.Motos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MotosApi {

    @GET("motos")
    Call<List<Motos>> getMotos();

    @POST("motos")
    Call<Motos> createMoto(@Body Motos motos);

    // Gọi API để cập nhật thông tin mô tô
    @PUT("motos/{id}")
    Call<Motos> updateMotos(@Path("id") String id, @Body Motos motos);

    // Gọi API để xoá thông tin mô tô
    @DELETE("motos/{id}")
    Call<Void> deleteMotos(@Path("id") String id);
}
