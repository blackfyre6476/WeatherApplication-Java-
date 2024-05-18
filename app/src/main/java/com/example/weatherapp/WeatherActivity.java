package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {
    private TextView cityWeather,temperatureWeather,weatherConditionWeather,humitdtyWeather,max_tempWeather,min_tempWeather,pressureWeather,windWeather;
    private ImageView imageView;
    private FloatingActionButton fabsave;
    private Button search;
    private EditText editTextSearch;
    LinearLayout bgweather,layout_search;
    Button saveLocation;
    String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityWeather=findViewById(R.id.textViewCitySearch);
        temperatureWeather=findViewById(R.id.textViewTempSearch);
        weatherConditionWeather=findViewById(R.id.textViewWeaatherConditionSearch);
        humitdtyWeather=findViewById(R.id.textHumididtySearch);
        pressureWeather=findViewById(R.id.textPressureSearch);
        windWeather=findViewById(R.id.textViewWindSearch);
        imageView=findViewById(R.id.imageViewSearch);
        search=findViewById(R.id.btnSearch);
        editTextSearch=findViewById(R.id.editTextCityName);
        max_tempWeather=findViewById(R.id.textMax_tempWeather);
        min_tempWeather=findViewById(R.id.textViewMin_tempWeather);
        bgweather=findViewById(R.id.bgweather);
        layout_search=findViewById(R.id.linear_layout_search);
        saveLocation=findViewById(R.id.buttonSave);
        fabsave=findViewById(R.id.floatingActionButtonsavelist);
        SharedPreferences preferences=getSharedPreferences(getPackageName(),MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();

        Set<String> backup=new HashSet<>();



       search.setOnClickListener(view -> {

           cityName=editTextSearch.getText().toString();

           getWeatherData(cityName);
          // editTextSearch.setText("");

       });

       saveLocation.setOnClickListener(view -> {
           cityName=editTextSearch.getText().toString();
           try {
                Set<String>  cities=preferences.getStringSet("cities",backup);
               cities.add(cityName);
               editor.putStringSet("cities",cities)
                               .apply();

               Toast.makeText(WeatherActivity.this, "Location saved :"+cityName, Toast.LENGTH_SHORT).show();
           } catch (Exception e){
               Log.e("error",e.toString());
           }

       });
       fabsave.setOnClickListener(view -> {
           if(preferences.getStringSet("cities",null)==null)
               Toast.makeText(WeatherActivity.this,"No Saved City",Toast.LENGTH_LONG).show();
           else
          startActivity( new Intent(WeatherActivity.this, SaveList.class));


       });

    }

    public void getWeatherData(String name){

        WeatherApi weatherApi=RetrofitWeather.getClient().create(WeatherApi.class);
        Call<Example> call=weatherApi.getWeatherWithCityName(name);
        Log.e("getWeatherData","before call enqueue");
        call.enqueue(new Callback<Example>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()) {
                    Log.e("getWeather data", "before set text");
                    cityWeather.setText(response.body().getName() + " , " + response.body().getSys().getCountry());
                    temperatureWeather.setText(response.body().getMain().getTemp() + " °C");
                    weatherConditionWeather.setText(response.body().getWeather().get(0).getDescription());
                    humitdtyWeather.setText(" : " + response.body().getMain().getHumidity());
                    max_tempWeather.setText(" : " + response.body().getMain().getTempMax() + " °C");
                    min_tempWeather.setText(" : " + response.body().getMain().getTempMin() + "°C");
                    pressureWeather.setText(" : " + response.body().getMain().getPressure() + " hPa");
                    windWeather.setText(" : " + response.body().getWind().getSpeed() + " km/h");


                    Log.e("getWeather", "onResponse");
                    String iconCode = response.body().getWeather().get(0).getIcon();
                    Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageView);

                    if(response.body().getSys().getSunset()<response.body().getDt()){
                        bgweather.setBackgroundResource(R.drawable.gradient_night);
                    }
                    else
                        bgweather.setBackgroundResource(R.drawable.gradient);
                    

                }
                else {
                    Toast.makeText(WeatherActivity.this, "invalid name", Toast.LENGTH_SHORT).show();
                }
                layout_search.setVisibility(View.VISIBLE);


            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.e("onFailure",t.toString());
            }
        });
    }
}
