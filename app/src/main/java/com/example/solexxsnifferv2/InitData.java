package com.example.solexxsnifferv2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitData {

    public static void sendGetRequest(String token) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                if (params.length == 0) {
                    return null;
                }
                String token = params[0];
                String response = "";

                try {
                    URL url = new URL(Common.GetEndPoint(EndPoints.INIT_DATA));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String postData = "token=" + token;

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = postData.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();

                    Log.i("REQ", "Server response: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        StringBuilder responseBuilder = new StringBuilder();
                        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                             BufferedReader bufferedReader = new BufferedReader(reader)) {
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                responseBuilder.append(line);
                            }
                        }
                        response = responseBuilder.toString();
                    } else {
                        response = "Error: " + responseCode;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Exception: " + e.getMessage();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.i("REQ", "result: " + result);
                if (result != null && !result.isEmpty()) {
                    Gson gson = new Gson();
                    try {
                        JsonArray jsonArray = gson.fromJson(result, JsonArray.class);
                        if (jsonArray.size() > 0) {
                            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                            if (jsonObject.has("autotrade")) {
                                String value = jsonObject.get("autotrade").getAsString();
                                if ("1".equals(value))
                                    Common.SetAutoTrade(true);
                                else
                                    Common.SetAutoTrade(false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(token);
    }
}
