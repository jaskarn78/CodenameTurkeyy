package com.example.android.hackathon.Utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tommy on 6/24/17.
 */
public class QueryJSONArray extends AsyncTask<String, Void, ArrayList<JSONObject>> {
    private static final String PHP_URL =
            "http://jagpal-development.com/food_truck/php/android_post_query.php";

    private ArrayList<JSONObject> jsonList;

    /**
     *  Asynchronously creates an HTTP connection to a php script. Uses a POST method to send the
     *  query (sent in the params parameter) to a PHP script. This script will query the database
     *  and return the resulting JSONArray.
     * @param params
     * @return
     */
    @Override
    protected ArrayList<JSONObject> doInBackground(String... params) {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL httpURL;

        // Get query string from params array
        String parameter = "query="+params[0];

        try
        {
            // Establish POST connection to the PHP URL
            httpURL = new URL(PHP_URL);
            connection = (HttpURLConnection) httpURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            // POST information to the PHP URL
            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameter);
            request.flush();
            request.close();

            String line = "";

            // Prepare to get input back from the PHP URL
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            // Read PHP response line by line
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            // You can perform UI operations here
            isr.close();
            reader.close();

            // Make sure there was a return value before sending information back
            if (!sb.toString().equals("")) {
                // Prepare to store response information as JSONObjects
                JSONArray jArray;
                jArray = new JSONArray(sb.toString());
                jsonList = new ArrayList<>();

                // Itterate through the JSONArray response and store JSONObjects in ArrayList
                for (int i = 0; i < jArray.length(); i++) {
                    jsonList.add(jArray.getJSONObject(i));
                    Log.v("PrintLine", jsonList.get(i).toString());
                }
            }
            // Return ArrayList of JSONObjects returned from query
            return jsonList;
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

}
