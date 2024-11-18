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

public class InitData {

    // Metoda wysyłająca zapytanie GET i przetwarzająca dane JSON
    public static void sendGetRequest(String token) {
        // Tworzymy nową AsyncTask do wykonywania zapytania w tle
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                // Sprawdzamy, czy params nie jest puste
                if (params.length == 0) {
                    return null;  // Jeśli brak parametru, kończymy zadanie
                }

                String token = params[0];  // Pierwszy element tablicy to nasz token
                String response = "";  // Zmienna, która przechowa odpowiedź serwera

                try {
                    URL url = new URL("https://capybara.s1.zetohosting.pl/initData.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String postData = "token=" + token;

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = postData.getBytes("utf-8");  // Konwertujemy dane na bajty
                        os.write(input, 0, input.length);  // Wysyłamy dane
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
                    try {

                        JSONObject jsonResponse = new JSONObject(result);

                        if (jsonResponse.has("autoTrade")) {
                            String value = jsonResponse.getString("autoTrade");
                            Log.i("REQ", "value: " + value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Jeśli odpowiedź jest pusta, możesz obsłużyć to tutaj
                }
            }

        }.execute(token);  // Przekazujemy token
    }
}
