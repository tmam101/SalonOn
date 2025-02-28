package com.example.salonon;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

// TODO Consider rxjava and retrofit

public class Network {
    public String herokuURL = "https://salon-on-backend.herokuapp.com/";
    public String herokuTestURL = "https://salon-on-backend-test-thomas.herokuapp.com/";

    public String get(String url) {
        try {
            NetworkGet task = new NetworkGet();
            return task.execute(url).get();
        } catch (Exception e){
            return null;
        }
    }

    // When creating a post request, first create a HashMap of <String, String> parameters and pass it in.
    public String post(String url, Map<String, String> parameters) {
        try {
            NetworkPost task = new NetworkPost();
            task.parameters = parameters;
            return task.execute(url).get();
        } catch (Exception e) {
            return null;
        }
    }

    // Network code must be run asynchronously.  This AsyncTask technique might have bugs
    // For example, rotating the phone might crash it.  We can adjust this later
    private class NetworkGet extends AsyncTask<String, Void, String> {

        private Exception exception;

        // Override method provided by asynctask.  This code is run upon NetworkGet.execute().
        protected String doInBackground(String... urls) {
            try {
                // Create a connection to the "test" endpoint on our server.
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");

                // Read the content returned from that endpoint.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                // Return the string.
                return content.toString();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

//        // TODO this might be useful, but so far I'm not using it.
//        protected void onPostExecute(String string) {
////            print(string);
//        }

    }

    private class NetworkPost extends AsyncTask<String, Void, String> {

        private Exception exception;
        public Map<String, String> parameters = new HashMap<>();

        // Override method provided by asynctask.  This code is run upon NetworkGet.execute().
        protected String doInBackground(String... urls) {
            try {
                // Create a connection to the "test" endpoint on our server.
                String queryParameters = getParametersString(parameters);
                URL url = new URL(urls[0] + queryParameters);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
//                out.writeBytes(getParametersString(parameters));
                out.flush();
                out.close();

                // Read the content returned from that endpoint.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                // Return the string.
                return content.toString();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }

    // Takes the Network's parameters and converts them to something that the server is able to parse via JSON.
    private String getParametersString(Map<String, String> parameters)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        result.append("?");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
