package com.example.weatherapp;

import android.util.Log;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather?appid=d59d22082b8d1bbfa96e5c0ed5b20eb8&units=metric")
    Call<Example> getWeatherWithLocation(@Query("lat")double lat,@Query("lon")double lon);

    @GET("weather?appid=d59d22082b8d1bbfa96e5c0ed5b20eb8&units=metric")
    Call<Example> getWeatherWithCityName(@Query("q")String name);
}
