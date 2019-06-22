package com.example.myapplication;// sorgt dafür das nachfolgende Codeelemente untergeordnete Elemente des Packages sind


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.example.myapplication.HttpRequest;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String COUNT_KEY = "counter";
    public static final String MESSAGE_KEY = "com.example.myapplication.id.message";

    public boolean powerstate = false;
    public int volume = 0;
    public boolean pipstate = false;
    public boolean debugstate = false;
    public boolean pipzoomstate = false;
    int counter = -1;


    private HttpRequest httpRequest; // HttpRequest object used to issue requests to the TV Server


    private int mSaveInstanceStateCounter = 0;

    private SharedPreferences mysharedPreferences;
    private ArrayList<Channel> channels = new ArrayList<Channel>();

    // The Handler object used to receive Message objects from Threads (since they can't write to widgets created by main thread)
    private Handler handler;

    private String mymsg;


    @Override // Dem Compiler wird gesagt dass die Nachfolgende Methode, die gleichnamige Methode aus der Basisklasse überschreiben soll
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Legt das Layout der App fest



        mysharedPreferences = this.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);

        // Initialize HTTP Request Instance; 10.0.2.2 is the machine's default IP address when accessed from emulator
        httpRequest = new HttpRequest("10.0.2.2", 1000, false);

        // Setze Zustand für Powerbtn
        try {
            channels = (ArrayList<Channel>) ObjectSerializer.deserialize(mysharedPreferences.getString("Data", ObjectSerializer.serialize(new ArrayList<Channel>())));
            Log.i("read from share",channels.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!(savedInstanceState == null)) {
            Log.i(TAG, "Trying to retrieve mSaveInstanceStateCounter... Current value: " + savedInstanceState.getInt(COUNT_KEY));
            this.mSaveInstanceStateCounter = savedInstanceState.getInt(COUNT_KEY);


            powerstate = savedInstanceState.getBoolean("powerbtn");

            volume = savedInstanceState.getInt("volume");

            pipstate = savedInstanceState.getBoolean("pipstate");

            debugstate = savedInstanceState.getBoolean("debugstate");

            pipzoomstate = savedInstanceState.getBoolean("pipzoomstate");


            counter = savedInstanceState.getInt("counter");
        }

        // Instantiate Handler that receives messages from threads or tasks
        this.handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                createChannelListFromJSON(msg.getData().getString(MESSAGE_KEY));
                try {
                    mysharedPreferences.edit().putString("Data", ObjectSerializer.serialize(channels)).apply();
                    Log.i("safe in share",ObjectSerializer.serialize(channels));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Connecte Navigation mit der Activity
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Setze default Item das angezeigt werden soll
        bottomNav.setSelectedItemId(R.id.navigation_Home);


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
                                break;
                            case R.id.navigation_Liste:
                                // TODO
                                Log.i("Info", "Nav_Liste wurde geklickt");
                                Intent liste = new Intent(MainActivity.this,Activity_liste.class);
                                startActivity(liste);
                                break;
                            case R.id.navigation_Favoriten:
                                // TODO
                                Log.i("Info", "Nav_Favoriten wurde geklickt");
                                Intent favorites = new Intent(MainActivity.this,Activity_favorites.class);
                                startActivity(favorites);
                                break;
                            case R.id.navigation_PipListe:
                                // TODO
                                Log.i("Info", "Nav_PipListe wurde geklickt");
                                Intent pipliste = new Intent(MainActivity.this,Activity_piplist.class);
                                startActivity(pipliste);
                                break;
                        }

                        return false;
                    }
                });


    }

    /* Klick auf powerbtn */
    public void klickpowerbtn(View view)
    {

        if(powerstate == false)
        {
            String command = "standby=0";
            commandToTV(command);
            Toast.makeText(this, "Screen On", Toast.LENGTH_SHORT).show();
            powerstate = true;
        } else
        {
            String command = "standby=1";
            commandToTV(command);
            Toast.makeText(this, "Screen Off", Toast.LENGTH_SHORT).show();
            powerstate = false;
        }

    }

    /* Scanne Channels wenn Channel Scan gedrückt wird */
    public void klickChannelScan(View view)
    {
        ScanChannelsTask task = new ScanChannelsTask(this.httpRequest, getApplicationContext(), this.handler);
        task.execute("scanChannels=");
    }

    /* Klick auf Playbtn */
    public void klickplaybtn(View view)
    {
        String command = "timeShiftPause=";
        commandToTV(command);
    }

    /* Klick auf Debugbtn */
    public void klickdebugbtn(View view)
    {
        if(debugstate == false)
        {
            String command = "debug=1";
            commandToTV(command);
            Toast.makeText(this, "Debug On", Toast.LENGTH_SHORT).show();
            debugstate = true;
        } else
        {
            String command = "debug=0";
            commandToTV(command);
            Toast.makeText(this, "Debug Off", Toast.LENGTH_SHORT).show();
            debugstate = false;
        }
    }

    /* Klick auf zoomPipbtn */
    public void klickzoomPipbtn(View view)
    {
        if(pipzoomstate == false)
        {
            String command = "zoomPip=1";
            commandToTV(command);
            Toast.makeText(this, "Pipstate On", Toast.LENGTH_SHORT).show();
            pipzoomstate = true;
        } else
        {
            String command = "zoomPip=0";
            commandToTV(command);
            Toast.makeText(this, "Pipstate Off", Toast.LENGTH_SHORT).show();
            pipzoomstate = false;
        };
    }

    /* Klick auf Pipbtn */
    public void klickpipbtn(View view)
    {
        if(powerstate == false)
        {
            String command = "showPip=1";
            commandToTV(command);
            Toast.makeText(this, "Pipstate On", Toast.LENGTH_SHORT).show();
            powerstate = true;
        } else
        {
            String command = "showPip=0";
            commandToTV(command);
            Toast.makeText(this, "Pipstate Off", Toast.LENGTH_SHORT).show();
            powerstate = false;
        };
    }

    /* Klick auf Killbtn */
    public void klickkillbtn(View view)
    {
        String command = "powerOff=";
        commandToTV(command);
    }

    /* Klick auf rshiftbtn */
    public void klickrshiftbtn(View view)
    {
        String command = "timeShiftPlay=10";
        commandToTV(command);
    }

    /* Klick auf rshiftbtn */
    public void klicklshiftbtn(View view)
    {
        String command = "timeShiftPlay=0";
        commandToTV(command);
    }

    /* Klick auf  VolumeUpbtn */
    public void klickVolumeUpbtn(View view)
    {
        if(volume == 100)
        {
            Toast.makeText(this, "Volume = " + volume, Toast.LENGTH_SHORT).show();
            return;
        }
        volume++;
        String command = "volume=" + volume;
        commandToTV(command);
        Toast.makeText(this, "Volume = " + volume, Toast.LENGTH_SHORT).show();
    }

    /* Klick auf  VolumeDownbtn */
    public void klickVolumeDownbtn(View view)
    {
        if(volume == 0)
        {
            Toast.makeText(this, "Volume = " + volume, Toast.LENGTH_SHORT).show();
            return;
        }
        volume--;
        String command = "volume=" + volume;
        commandToTV(command);
        Toast.makeText(this, "Volume = " + volume, Toast.LENGTH_SHORT).show();
    }

    /* Klick auf  zoomInbtn */
    public void klickzoomInbtn(View view)
    {
        String command = "zoomMain=1";
        commandToTV(command);
    }

    /* Klick auf  zoomOutbtn */
    public void klickzoomOutbtn(View view)
    {
        String command = "zoomMain=0";
        commandToTV(command);
    }

    /* Klick auf  zappUpbtnbtn */
    public void klickzappUpbtn(View view)
    {

        if(channels.isEmpty())
        {
            Toast.makeText(this, "Press pls Scan Channels", Toast.LENGTH_SHORT).show();


        } else
        {
            if(counter <= channels.size())
            {
                counter++;

                if (counter == channels.size())
                {
                    counter = 0;
                }

                // Counter ist noch im ArrayBereich
                String currentchannel = channels.get(counter).getChannel();
                String command = "channelMain=" + currentchannel;
                commandToTV(command);





            }

        }

    }

    /* Klick auf  zappDownbtn */
    public void klickzappDownbtn(View view)
    {

        if(channels.isEmpty())
        {
            Toast.makeText(this, "Press pls Scan Channels", Toast.LENGTH_SHORT).show();


        } else
        {
            if(counter <= channels.size())
            {
                counter--;

                if (counter < 0)
                {
                    counter = channels.size()-1;
                }
                // Counter ist noch im ArrayBereich
                String currentchannel = channels.get(counter).getChannel();
                String command = "channelMain=" + currentchannel;
                commandToTV(command);





            }

        }

    }


    public void sendMessageToActivity(View view) {

        if (mymsg != null)
        {
            Intent intent = new Intent(this, Activity_liste.class);
            String message = mymsg;
            Log.i("INFO", mymsg);
            intent.putExtra("Data", message);
            startActivity(intent);
        } else
        {
            Toast.makeText(this, "Press pls Scan Channels", Toast.LENGTH_SHORT).show();
        }

    }


    protected void createChannelListFromJSON(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            // txtLog.setText(json.names().toString());
            JSONArray array = json.getJSONArray("channels");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Channel c = new Channel(
                        obj.getString("frequency"),
                        obj.getString("channel"),
                        obj.getInt("quality"),
                        obj.getString("program"),
                        obj.getString("provider"));
                channels.add(c);
                Log.i("Create ChannelList",c.getProgram());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void commandToTV(final String command) {
//      JSONObject object = this.httpRequest.execute(message); // called directly from UI Thread --> doesn't work
        /*
        Better use an anonymous AsyncTask-object that issues a command to the TV Server via implementing the Runnable Interface
         */
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = httpRequest.execute(command);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.i("INFO:", "Ist in onSaveInstanceState");


        savedInstanceState.putBoolean("powerbtn",powerstate);
        savedInstanceState.putInt("volume",volume);
        savedInstanceState.putBoolean("pipstate", pipstate);
        savedInstanceState.putBoolean("debugstate", debugstate);
        savedInstanceState.putBoolean("debugstate", pipzoomstate);
        savedInstanceState.putInt("counter",volume);

        super.onSaveInstanceState(savedInstanceState);
    }

}


