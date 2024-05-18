package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class activitySplash extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    double lat,lon;
    String city,temperature,weatherCondition, humitdty,max_temp,min_temp,pressure, wind,feelslike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                lat=location.getLatitude();
                lon=location.getLongitude();
                Log.e("lat:",String.valueOf(lat));
                Log.e("lon:",String.valueOf(lon));

                getWeatherData(lat,lon);
            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,50,locationListener);
        }






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && permissions.length>0&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,50,locationListener);
        }
    }

    public void getWeatherData(double lat,double lon){

        Intent intent=new Intent(this, MainActivity.class);


        WeatherApi weatherApi=RetrofitWeather.getClient().create(WeatherApi.class);
        Call<Example> call=weatherApi.getWeatherWithLocation(lat, lon);Log.e("getWeatherData","before call enqueue");
        call.enqueue(new Callback<Example>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Log.e("getWeather data","before set text");
                city=response.body().getName()+" , "+response.body().getSys().getCountry();
                temperature=response.body().getMain().getTemp()+" °C";
                weatherCondition=response.body().getWeather().get(0).getDescription();
                humitdty="  "+response.body().getMain().getHumidity()+"%";
                max_temp=""+response.body().getMain().getTempMax()+"°C";
                min_temp=" / "+response.body().getMain().getTempMin()+"°C";
                pressure="  "+response.body().getMain().getPressure()+" hPa";
                wind="  "+response.body().getWind().getSpeed()+" m/sec";



                Integer sunsetime=response.body().getSys().getSunset();



                Integer current_time= Math.toIntExact(System.currentTimeMillis() / 1000);

                if(sunsetime < current_time){
                    intent.putExtra("bgset",true);
                }
                else intent.putExtra("bgset",false);




                feelslike="Feels Like "+response.body().getMain().getFeelsLike();


                intent.putExtra("city",city);
                intent.putExtra("temperature",temperature);
                intent.putExtra("weatherCondition",weatherCondition);
                intent.putExtra("humitdty",humitdty);
                intent.putExtra("max_temp",max_temp);
                intent.putExtra("min_temp",min_temp);
                intent.putExtra("wind",wind);
                intent.putExtra("pressure",pressure);
                intent.putExtra("feelslike",feelslike);
                String iconCode=response.body().getWeather().get(0).getIcon();
                intent.putExtra("iconCode",iconCode);


                startActivity(intent);
                finish();


            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.e("onFailure",t.toString());
            }
        });
    }
}