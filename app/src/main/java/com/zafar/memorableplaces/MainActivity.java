package com.zafar.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    static SharedPreferences sharedPreferences;
    static HashSet<String> set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);




        sharedPreferences = getApplicationContext().getSharedPreferences("com.zafar.memorableplaces", Context.MODE_PRIVATE);
        set = (HashSet<String>)sharedPreferences.getStringSet("placesList", null);


        if (set == null){
            places.add("Add A New Memorable Place...");
        }else{
            places = new ArrayList(set);

        }

        // Makes sure that Adding A New Memorable Place is at the top of the list
        for(int i=0;i<places.size();i++){
            if(this.places.get(i).equals("Add A New Memorable Place...")){
                Collections.swap(places, i, 0);

            }
        }



       // places.add(0,"Add A New Memorable Place...");



        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                    intent.putExtra("placeID", position);
                    startActivity(intent);
                }
            }
        });
    }


}
