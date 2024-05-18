package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView city, temperature, weatherCondition, humitdty, max_temp, min_temp, pressure, wind ,feelslike ;
    private ImageView imageView;
    private FloatingActionButton fab,fabs;
    private LinearLayout bgmain;

    LocationManager locationManager;
    LocationListener locationListener;
    double lat, lon;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgmain = findViewById(R.id.bgmain);
        city = findViewById(R.id.textViewCity);
        temperature = findViewById(R.id.textViewTemp);
        weatherCondition = findViewById(R.id.textViewWeaatherCondition);
        humitdty = findViewById(R.id.textHumididty);
        max_temp = findViewById(R.id.textMax_temp);
        min_temp = findViewById(R.id.textViewMin_temp);
        pressure = findViewById(R.id.textPressure);
        wind = findViewById(R.id.textViewWind);
        imageView = findViewById(R.id.imageView);
        fab = findViewById(R.id.floatingActionButton);
        feelslike = findViewById(R.id.textViewFeelslike);
        fabs=findViewById(R.id.floatingActionButtonsave);


        Intent recv = getIntent();

        Bundle b = recv.getExtras();


        city.setText(b.getString("city"));
        temperature.setText(b.getString("temperature"));
        weatherCondition.setText(b.getString("weatherCondition"));
        humitdty.setText(b.getString("humitdty"));
        max_temp.setText(b.getString("max_temp"));
        min_temp.setText(b.getString("min_temp"));
        pressure.setText(b.getString("pressure"));
        wind.setText(b.getString("wind"));
        feelslike.setText(b.getString("feelslike"));

        String iconCode = b.getString("iconCode");
        Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);

        if (b.getBoolean("bgset")) {
            bgmain.setBackgroundResource(R.drawable.gradient_night);
        } else {
            bgmain.setBackgroundResource(R.drawable.gradient);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });
        fabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences=getSharedPreferences(getPackageName(),MODE_PRIVATE);
                if(preferences.getStringSet("cities",null)==null)
                    Toast.makeText(MainActivity.this,"No Saved City",Toast.LENGTH_LONG).show();
                else
                    startActivity(new Intent(MainActivity.this,SaveList.class));
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.e("lat:", String.valueOf(lat));
                Log.e("lon:", String.valueOf(lon));

                getWeatherData(lat,lon);
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, locationListener);
        }
    }


    public void getWeatherData(double lat, double lon) {

        WeatherApi weatherApi = RetrofitWeather.getClient().create(WeatherApi.class);
        Call<Example> call = weatherApi.getWeatherWithLocation(lat, lon);
        Log.e("getWeatherData", "before call enqueue");
        call.enqueue(new Callback<Example>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Log.e("getWeather data", "MainActivity");
                city.setText(response.body().getName() + " , " + response.body().getSys().getCountry());
                temperature.setText(response.body().getMain().getTemp() + " °C");
                weatherCondition.setText(response.body().getWeather().get(0).getDescription());
                humitdty.setText("  " + response.body().getMain().getHumidity() + "%");
                max_temp.setText("" + response.body().getMain().getTempMax() + "°C");
                min_temp.setText(" / " + response.body().getMain().getTempMin() + "°C");
                pressure.setText("  " + response.body().getMain().getPressure() + " hPa");
                wind.setText("  " + response.body().getWind().getSpeed() + " m/sec");
                feelslike.setText("Feels Like " + response.body().getMain().getFeelsLike());


                Log.e("getWeather", "onResponse");
                String iconCode = response.body().getWeather().get(0).getIcon();
                Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imageView);


            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }
}
