package com.zafar.memorableplaces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    static SharedPreferences sharedPreferences;

    static HashSet<String> set;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);




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

                double latDouble = 0.0;
                double longDouble = 0.0;
                String address = "";
                if(position !=0){
                    Scanner scan = new Scanner((String)parent.getItemAtPosition(position));
                    scan.useDelimiter(":");
                    scan.next();
                    address = scan.next().trim();
                    address = address.substring(0, address.length() - 3);
                    String latitude = scan.next().trim();
                    latitude = latitude.substring(0,latitude.length()-5);
                    String longitude = scan.next().trim();
                    longitude = longitude.substring(0, latitude.length() - 1);
                    latDouble = Double.parseDouble(latitude);
                    longDouble = Double.parseDouble(longitude);
                }
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);


                intent.putExtra("placeID", position);
                intent.putExtra("long", longDouble);
                intent.putExtra("lat", latDouble);
                intent.putExtra("address", address);

                startActivity(intent);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemToDelete = position;
                if (itemToDelete != 0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to delete this place?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    places.remove(itemToDelete);
                                    arrayAdapter.notifyDataSetChanged();

                                    HashSet<String> set = new HashSet<>(MainActivity.places);
                                    sharedPreferences.edit().putStringSet("placesList", set).apply();
                                    final Toast toast = Toast.makeText(getApplicationContext(), "Place Deleted",
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, 800);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                    return true;

            }
        });
    }



}
