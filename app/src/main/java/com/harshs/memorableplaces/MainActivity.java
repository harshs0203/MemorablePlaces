package com.harshs.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> arrayList;
    static ArrayList<LatLng> locationList;
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* SharedPreferences sharedPreferences=this.getSharedPreferences("com.harshs.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<String>();
        ArrayList<String> longitudes = new ArrayList<String>();

        arrayList.clear();
        latitudes.clear();
        longitudes.clear();
        locationList.clear();

        try{

            arrayList= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lat",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lon",ObjectSerializer.serialize(new ArrayList<String>())));

        }catch(Exception e){
            e.printStackTrace();
        }

        if(arrayList.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0){
            if(arrayList.size() == latitudes.size() && arrayList.size() == longitudes.size()){

                for(int i= 0; i < latitudes.size(); i++){
                    locationList.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }

            }
        }else{
            arrayList.add("Add a New Place");
            locationList.add(new LatLng(0,0));
         }*/

        ListView listView= (ListView) findViewById(R.id.listView);

       arrayList =  new ArrayList<String>();
       locationList = new ArrayList<LatLng>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent =new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeNumber",position);
                startActivity(intent);

            }
        });


    }
}
