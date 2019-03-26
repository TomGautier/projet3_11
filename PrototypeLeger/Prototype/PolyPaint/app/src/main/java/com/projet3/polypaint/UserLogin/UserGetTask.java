package com.projet3.polypaint.UserLogin;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class UserGetTask extends AsyncTask<String, String, JSONArray> {
    protected JSONArray doInBackground(String... urls) {
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
            //StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            JSONArray jsonArray = new JSONArray();
            while ((line = rd.readLine()) != null) {
                //response.append(line);
                if (line == "403")
                    return null;
                jsonArray = new JSONArray(line);
               // response.append('\r');
            }
            rd.close();
            //return response.toString();
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /*@Override
    protected void onPostExecute(ArrayList<JSONObject> s) {
        super.onPostExecute(s);
        RequestManager.currentInstance.setUserConversations(s);
    }*/
}
