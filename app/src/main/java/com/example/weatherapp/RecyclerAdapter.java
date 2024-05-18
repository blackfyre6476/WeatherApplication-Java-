package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CityViewHolder> {
    String cityName;
    Set<String> name;



    public RecyclerAdapter(String cityName, Set<String> name) {
        this.cityName = cityName;
        this.name = name;


    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout,parent,false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        
        if(name.toArray() ==null){
            holder.city.setText("Add a city");
        }
        else {
           holder.getWeatherData(name.toArray()[holder.getLayoutPosition()].toString());
        }

    }

    @Override
    public int getItemCount() {
        if(name.size()<0)
            return 0;
        return name.size();
    }



    public class CityViewHolder extends RecyclerView.ViewHolder{
        TextView city,temp;
        ImageView icon;
        LinearLayout recyclerlinear;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);

            city=itemView.findViewById(R.id.citylist);
            temp=itemView.findViewById(R.id.templist);
            icon=itemView.findViewById(R.id.iconlist);
            recyclerlinear=itemView.findViewById(R.id.recyclerlinear);


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
                        city.setText(response.body().getName() + " , " + response.body().getSys().getCountry());
                        temp.setText(response.body().getMain().getTemp() + " °C");
                        //max_tempWeather.setText(" : " + response.body().getMain().getTempMax() + " °C");
                        //min_tempWeather.setText(" : " + response.body().getMain().getTempMin() + "°C");


                        Log.e("getWeather", "onResponseSaveList");
                        String iconCode = response.body().getWeather().get(0).getIcon();
                        Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(icon);

                        if(response.body().getSys().getSunset()<response.body().getDt()){
                            recyclerlinear.setBackgroundResource(R.drawable.gradient_night);
                        }
                        else
                            recyclerlinear.setBackgroundResource(R.drawable.gradient);


                    }
                    else {
                        //Toast.makeText(, "invalid name", Toast.LENGTH_SHORT).show();
                    }



                }

                @Override
                public void onFailure(Call<Example> call, Throwable t) {
                    Log.e("onFailure",t.toString());
                }
            });
    }

    }
}
