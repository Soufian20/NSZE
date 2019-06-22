package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;

public class Activity_piplist extends AppCompatActivity {

    private static final String TAG = "PipListeActivity";

    private ArrayList<Channel> pipchannels = new ArrayList<Channel>();
    private SharedPreferences mysharedPreferences;

    //for PipListe
    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager mylayoutManager;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piplist);

        // Connecte Navigation mit der Activity
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Setze default Item das angezeigt werden soll
        bottomNav.setSelectedItemId(R.id.navigation_PipListe);


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
                                Intent home = new Intent(Activity_piplist.this,MainActivity.class);
                                startActivity(home);
                                break;
                            case R.id.navigation_Liste:
                                // TODO
                                Log.i("Info", "Nav_Liste wurde geklickt");
                                Intent liste = new Intent(Activity_piplist.this,Activity_liste.class);
                                startActivity(liste);
                                break;
                            case R.id.navigation_Favoriten:
                                // TODO
                                Log.i("Info", "Nav_Favoriten wurde geklickt");
                                Intent favorites = new Intent(Activity_piplist.this,Activity_favorites.class);
                                startActivity(favorites);
                                break;
                            case R.id.navigation_PipListe:
                                // TODO
                                Log.i("Info", "Nav_PipListe wurde geklickt");
                                break;
                        }

                        return false;
                    }
                });


        mysharedPreferences = this.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);

        //### For RecylerView ###
        // Lookup the recyclerview in activity layout
        myrecyclerView = (RecyclerView) findViewById(R.id.pipchannel_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        myrecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager(mylayoutManager);


        try {
            pipchannels = (ArrayList<Channel>) ObjectSerializer.deserialize(mysharedPreferences.getString("Data", ObjectSerializer.serialize(new ArrayList<Channel>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // specify an adapter

        myAdapter = new piplist_adapter(pipchannels);
        myrecyclerView.setAdapter(myAdapter);
        //###
    }
}
