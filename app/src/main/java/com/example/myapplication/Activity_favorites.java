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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Activity_favorites extends AppCompatActivity {

    private static final String TAG = "FavoritesActivity";

    private ArrayList<Channel> favchannels = new ArrayList<Channel>();
    private SharedPreferences mysharedPreferences;

    //for RecyclerView2
    private RecyclerView myrecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager mylayoutManager;
    private Handler handler;
    private Object ItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mysharedPreferences = this.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);

        //### For RecylerView ###
        // Lookup the recyclerview in activity layout
        myrecyclerView = (RecyclerView) findViewById(R.id.Favchannel_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        myrecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mylayoutManager = new LinearLayoutManager(this);
        myrecyclerView.setLayoutManager(mylayoutManager);


        try {
            favchannels = (ArrayList<Channel>) ObjectSerializer.deserialize(mysharedPreferences.getString("FavData", ObjectSerializer.serialize(new ArrayList<Channel>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Hole das geschickte Intent
        Intent i = getIntent();
        if (i.getExtras() != null)
        {
            Log.i("INFO", "Intent wurde geschickt Array ist grad  " + favchannels.size());
            Channel c = (Channel) i.getExtras().getSerializable("FavChannel");

            // Schauen ob nicht schon im Array drinne ist
            if(favchannels.indexOf(c.getChannel()) == -1)
            {
                // Ist nicht drinne also push in Array
                favchannels.add(c);
                Log.i("INFO", "Channel von Intent wurde hinzugefügt , Array ist jetzt  " + favchannels.size());
                try
                {
                    mysharedPreferences.edit().putString("FavData", ObjectSerializer.serialize(favchannels)).apply();
                    Log.i("INFO", "Array nach add wird gesaftet und ist " + favchannels.size());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                //ist drinne also push es nicht
                Log.i("INFO", "Channel von Intent ist bereits im Array, Array ist " + favchannels.size());
                Toast.makeText(this, c.getProgram() + " ist schon in den Favoriten", Toast.LENGTH_SHORT).show();
            }
        }


        // specify an adapter

        myAdapter = new MyAdapter2(favchannels);
        myrecyclerView.setAdapter(myAdapter);
        //###







        // Connecte Navigation mit der Activity
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Setze default Item das angezeigt werden soll
        bottomNav.setSelectedItemId(R.id.navigation_Favoriten);


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
                                Intent home = new Intent(Activity_favorites.this,MainActivity.class);
                                startActivity(home);
                                break;
                            case R.id.navigation_Liste:
                                // TODO
                                Log.i("Info", "Nav_Liste wurde geklickt");
                                Intent liste = new Intent(Activity_favorites.this,Activity_liste.class);
                                startActivity(liste);
                                break;
                            case R.id.navigation_Favoriten:
                                // TODO
                                Log.i("Info", "Nav_Favoriten wurde geklickt");
                                break;
                            case R.id.navigation_PipListe:
                                // TODO
                                Log.i("Info", "Nav_PipListe wurde geklickt");
                                Intent pipliste = new Intent(Activity_favorites.this,Activity_piplist.class);
                                startActivity(pipliste);
                                break;
                        }

                        return false;
                    }
                });
    }

    // Wenn Activity wechselt soll der Zustand gespeichert werden
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.i("INFO:", "Ist in onSaveInstanceState");
        try {
            mysharedPreferences.edit().putString("FavData", ObjectSerializer.serialize(favchannels)).apply();
            Log.i("safe in share",ObjectSerializer.serialize(favchannels));
            Log.i("INFO", "aktueller Array wurde gesafed " + favchannels.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onSaveInstanceState(savedInstanceState);
    }

}
