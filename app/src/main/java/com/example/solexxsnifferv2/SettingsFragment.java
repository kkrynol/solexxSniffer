package com.example.solexxsnifferv2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsFragment extends Fragment {
    private EditText textToken;
    private EditText textApp;
    private EditText textServer;
    private String tokenValue;
    private static CheckBox checkboxAutoTrade;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textToken = view.findViewById(R.id.textToken);
        textApp = view.findViewById(R.id.textApp);
        textServer = view.findViewById(R.id.textServer);
        checkboxAutoTrade = view.findViewById(R.id.checkboxAutoTrade);
        checkboxAutoTrade.setChecked(Common.GetAutoTrade());

        Button applyButton = view.findViewById(R.id.buttonApplySettings);
        applyButton.setOnClickListener(v -> applySettings());

        return view;
    }
    public  void applySettings() {

        String newToken = textToken.getText().toString().trim();
        String newAppPackage = textApp.getText().toString().trim();
        String newServerUrl = textServer.getText().toString().trim();
        boolean autoTrade = checkboxAutoTrade.isChecked();

        Common.UpdateSettings(newAppPackage, newServerUrl, newToken, autoTrade);
        sendPostRequest(newToken);
    }
    @SuppressLint("StaticFieldLeak")
    private void sendPostRequest(String token) {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                String token = params[0];

                try {
                    URL url = new URL(Common.GetEndPoint(EndPoints.SETTINGS));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    int at = 0;
                    if(Common.GetAutoTrade()) at = 1;

                    String postData = "token=" + token + "&autotrade=" + at;

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = postData.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //ok
                    } else {
                        Log.i("req", "Server response: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

        }.execute(token);
    }
}
