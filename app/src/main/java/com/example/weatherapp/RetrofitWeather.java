package com.example.weatherapp;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitWeather {

    private static Retrofit retrofit;

    public static Retrofit getClient(){

        if(retrofit==null){
            Log.e("getClient","retrofit");
            retrofit=new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
