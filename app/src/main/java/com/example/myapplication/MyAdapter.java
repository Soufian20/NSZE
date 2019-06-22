package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private ArrayList<Channel> mDataset;
    private SharedPreferences mysharedPreferences;


    private Handler handler;
    private HttpRequest httpRequest = new HttpRequest("10.0.2.2", 1000, false);

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    // Hat die Aufgabe die Viewobjekte zu halten
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public TextView myTextView;
        public ImageButton mybtn;





        public MyViewHolder(final View v) {
            super(v);

            myTextView = (TextView) v.findViewById(R.id.program_name);
            mybtn = (ImageButton) v.findViewById(R.id.btnAddFavorite);

            v.setOnClickListener(this);

            v.getContext().getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE);

            // Listener der auf Herz Button horscht
            mybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Channel c = mDataset.get(pos);

                        // hole array mit Favoriten




                        Toast.makeText(v.getContext(), c.getProgram() + " zu Favoriten hinzugefügt", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(v.getContext(), Activity_favorites.class);
                        i.putExtra("FavChannel", c);
                        v.getContext().startActivities(new Intent[]{i});

                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            String command = "channelMain=";
            command +=  mDataset.get(this.getAdapterPosition()).getChannel();
            Toast.makeText(v.getContext(), command, Toast.LENGTH_SHORT).show();
            selectChannel(command);

        }


    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Channel> myDataset) {
        mDataset = myDataset;
        Log.i("Adapter mdataset", mDataset.toString());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View v = inflater.inflate(R.layout.list_row, parent, false);

        // Return a new holder instance
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // Get the data model based on position
        Channel c = mDataset.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.myTextView;
        textView.setText(c.getProgram());

        ImageButton mybtn = holder.mybtn;
        mybtn.animate();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void selectChannel(final String command) {
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

}
