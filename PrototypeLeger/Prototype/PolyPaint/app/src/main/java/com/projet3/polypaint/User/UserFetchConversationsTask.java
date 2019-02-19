package com.projet3.polypaint.User;

import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class UserFetchConversationsTask extends AsyncTask<String, String, ArrayList<JSONObject>> {
    protected ArrayList<JSONObject> doInBackground(String... urls) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            /*connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);*/

            //Send request
            //DataOutputStream wr = new DataOutputStream (
              //      connection.getOutputStream());
            //wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            ArrayList<JSONObject> jsonObjects = new ArrayList<>();
            while ((line = rd.readLine()) != null) {
                //response.append(line);
                jsonObjects.add(new JSONObject(line));
               // response.append('\r');
            }
            rd.close();
            //return response.toString();
            return jsonObjects;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(ArrayList<JSONObject> s) {
        super.onPostExecute(s);
        UserManager.currentInstance.setUserConversations(s);
    }
}
