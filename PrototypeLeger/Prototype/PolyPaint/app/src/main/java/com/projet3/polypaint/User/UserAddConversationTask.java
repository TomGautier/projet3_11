package com.projet3.polypaint.User;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class UserAddConversationTask extends AsyncTask<String, String, String> {
    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                if (line == "403")
                    return "";
               response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
