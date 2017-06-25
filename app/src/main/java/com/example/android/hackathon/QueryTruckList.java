package com.example.android.hackathon;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Tommy on 6/24/17.
 */

public class QueryTruckList extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
    private static final String URL = "https://script.google.com/macros/s/AKfycbygukdW3tt8sCPcFDlkMnMuNu9bH5fpt7bKV50p2bM/exec?id=1jHw3XQ1OIqCVefW9uiL2jtkZQAq8EXIkkljo1bSYThk&sheet=Sheet1";
    private JSONObject truckJSON;
    private ArrayList<JSONObject> truckList;
    private ArrayList<JSONObject> data;

    public QueryTruckList(ArrayList<JSONObject> data) {
        this.data = data;
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(Void... params) {

        OutputStreamWriter wr = null;
        BufferedReader reader = null;
        try {
            String data;
            if (truckJSON != null)
                data = truckJSON.toString();  // data is the JSONObject being sent to the php server
            else
                return null;

            // Connect to the URL
            java.net.URL url = new URL(URL);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            // POST the information to the URL
            wr.write(data);
            wr.flush();

            // Create a means to read the output from the PHP
            reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                Log.v("PrintLine", line);
                sb.append(line);
            }

            JSONArray jArray;
            jArray = new JSONArray(sb.toString());
            truckList = new ArrayList<JSONObject>();

            for (int i = 0; i < jArray.length(); i++) {
                truckList.add(jArray.getJSONObject(i));
                Log.v("PrintLine", truckList.get(i).toString());
            }

            wr.close(); // close OutputStreamWriter
            reader.close(); // close BufferedReader

            return truckList;
        } catch (MalformedURLException e) {
            Log.e("MalformedURL", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<JSONObject> objs) {
        if (objs != null) {
            setEventList(objs);
            data=objs;
        }

    }

    public ArrayList<JSONObject> getList(){
        return data;
    }

    public void setEventList(ArrayList<JSONObject> list){
        truckList = list;
    }
}
