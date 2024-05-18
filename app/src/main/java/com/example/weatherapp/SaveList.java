package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SaveList extends AppCompatActivity {
    String cityName;
    RecyclerAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_list);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SaveList.this));

        SharedPreferences prefs = getSharedPreferences(getPackageName(),MODE_PRIVATE);

        Set<String> fetch=prefs.getStringSet("cities",null );



        adapter=new RecyclerAdapter(cityName, fetch);
        recyclerView.setAdapter(adapter);

        Log.d("prefernces",fetch.toString());
        ItemTouchHelper touchHelper=new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //fetch.remove(viewHolder.getLayoutPosition());
                fetch.remove(fetch.toArray()[viewHolder.getLayoutPosition()].toString());
                prefs.edit().remove("cities");
                prefs.edit().putStringSet("cities",fetch);
                prefs.edit().apply();

                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                Log.d("deleted prefernces",fetch.toString());
            }
        });


        touchHelper.attachToRecyclerView(recyclerView);

    }




}