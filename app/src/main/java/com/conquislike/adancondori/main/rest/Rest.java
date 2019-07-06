package com.conquislike.adancondori.main.rest;

import com.conquislike.adancondori.model.Dato;
import com.conquislike.adancondori.model.Specialties;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Rest {
    @GET("specialties/")
    Call<Dato> loadChanges(@Query("q") String status);
}


