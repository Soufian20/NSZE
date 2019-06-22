package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Activity_liste extends AppCompatActivity {

    private static final String TAG = "ListeActivity";
    public static final String MESSAGE_KEY = "com.example.myapplication.id.message";

    private ArrayList<Channel> channels = new ArrayList<Channel>();
    private SharedPreferences mysharedPreferences;

    //for RecyclerView
    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager mylayoutManager;
    private Handler handler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);


        // Kontext muss der gleiche sein um auf den gleichen shared zuzugreifen
        mysharedPreferences = this.getSharedPreferences("com.example.myapplication", Context.MODE_WORLD_READABLE);


        //### For RecylerView ###
        // Lookup the recyclerview in activity layout
        myrecyclerView = (RecyclerView) findViewById(R.id.channel_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        myrecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager(mylayoutManager);


        // Lese von shared und setzte in Adapter
        try {
            channels = (ArrayList<Channel>) ObjectSerializer.deserialize(mysharedPreferences.getString("Data", ObjectSerializer.serialize(new ArrayList<Channel>())));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("INFO read from shared", channels.toString());

        // specify an adapter
        myAdapter = new MyAdapter(channels);
        myrecyclerView.setAdapter(myAdapter);
        //###

        // Connecte Navigation mit der Activity
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Setze default Item das angezeigt werden soll
        bottomNav.setSelectedItemId(R.id.navigation_Liste);


        // Horsche auf Item clicks und erstelle bei Item klick das jeweilige Fragment
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.navigation_Home:
                                // TODO
                                Log.i("Info", "Nav_Home wurde geklickt");
                                Intent home = new Intent(Activity_liste.this,MainActivity.class);
                                startActivity(home);
                                break;
                            case R.id.navigation_Liste:
                                // TODO
                                Log.i("Info", "Nav_Liste wurde geklickt");
                                break;
                            case R.id.navigation_Favoriten:
                                // TODO
                                Log.i("Info", "Nav_Favoriten wurde geklickt");
                                Intent favorites = new Intent(Activity_liste.this,Activity_favorites.class);
                                startActivity(favorites);
                                break;
                            case R.id.navigation_PipListe:
                                // TODO
                                Log.i("Info", "Nav_PipListe wurde geklickt");
                                Intent pipliste = new Intent(Activity_liste.this,Activity_piplist.class);
                                startActivity(pipliste);
                                break;
                        }

                        return false;
                    }
                });
    }
}
