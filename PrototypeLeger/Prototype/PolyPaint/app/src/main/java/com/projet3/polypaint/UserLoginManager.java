package com.projet3.polypaint;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UserLoginManager {
    private final int TIMEOUT_DELAY = 5;
    private final String PORT =":3000";

    private String url;
    private String ip;
    private String loginID;

    public static UserLoginManager currentInstance;

    public UserLoginManager(String ip_) {
        ip = ip_;
    }

    public Boolean requestLogin(UserInformation userInformation_) {
        url = formatUrl(userInformation_, Request.Connection);
        UserLoginTask loginTask = new UserLoginTask();
        loginTask.execute(url);
        try{
            return configureResponse(loginTask.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean configureResponse(String response_){
        loginID = response_;
        return response_.isEmpty() ? false : true;
    }
    private String formatUrl(UserInformation userInformation_, String request){
        return "http://" + ip + PORT + request + userInformation_.getUsername()+ "/"
                + userInformation_.getPassword();
    }
    public final String getLoginID(){
        return loginID;
    }

}
class UserLoginTask extends AsyncTask<String, String, String> {
    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
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
final class Request {

    public static final String Connection = "/connection/login/";
    public static final String Sign_Up = "/connection/signup/";

}
