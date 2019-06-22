package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.example.myapplication.HttpRequest;

/**
 * Created by zander on 14.06.17.
 */

public class ScanChannelsTask extends AsyncTask<String, Integer, JSONObject> {

    public static final String TAG = "ScanChannelsTask";

    private Context context;
    private HttpRequest request;
    private boolean running;
    private Handler handler;


    public ScanChannelsTask(HttpRequest request, Context context, Handler handler) {
        this.request = request;
        this.context = context;
        this.handler = handler;
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    @Override
    protected JSONObject doInBackground(String[] params) {
        JSONObject obj = null;

        if (params[0] != null) {
            try {
//                Log.e(TAG, params[0] + " - " + this.request.getIpAddress());
                obj = this.request.sendHttp(params[0]);
//                Log.e(TAG, "Channel Scan almost finished...");
                Log.e(TAG, obj.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
        return obj;
    }


    @Override
    protected void onPreExecute() {
//        super.onPreExecute();
        Log.e(TAG, "onPreExecute() was called...");
        Toast.makeText(this.context, "Started Channel Scan", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(jsonObject != null) {
            super.onPostExecute(jsonObject);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.MESSAGE_KEY, jsonObject.toString());
            msg.setData(bundle);
            this.handler.sendMessage(msg);
            Toast.makeText(this.context, "Channel Scan finished", Toast.LENGTH_SHORT).show();

        }
    }
}
