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
    private CheckBox checkboxEnableNotify;
    private String tokenValue = "KtE35AKrlEoUlo5xjWFPzWs0CYvWdhATqrakqlxj2Mbg9ZxRTFWlIHh1xTL5wBqf";
    private String telegramPackageName = "org.telegram.messenger";
    private String postUrl = "https://capybara.s1.zetohosting.pl/add.php";
    private boolean enableNotify = false;
    private int autoTrade = 0;
    private CheckBox checkboxAutoTrade;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textToken = view.findViewById(R.id.textToken);
        textApp = view.findViewById(R.id.textApp);
        textServer = view.findViewById(R.id.textServer);
        checkboxEnableNotify = view.findViewById(R.id.checkboxEnableNotify);
        checkboxAutoTrade = view.findViewById(R.id.checkboxAutoTrade);

        Button applyButton = view.findViewById(R.id.buttonApplySettings);

        applyButton.setOnClickListener(v -> {

            String newToken = textToken.getText().toString().trim();
            String newAppPackage = textApp.getText().toString().trim();
            String newServerUrl = textServer.getText().toString().trim();


            enableNotify = checkboxEnableNotify.isChecked();

            if(checkboxAutoTrade.isChecked()) {
                autoTrade = 1;
            } else {
                autoTrade = 0;
            }


            tokenValue = newToken.isEmpty() ? tokenValue : newToken;
            telegramPackageName = newAppPackage.isEmpty() ? telegramPackageName : newAppPackage;
            postUrl = newServerUrl.isEmpty() ? postUrl : newServerUrl;

            CustomNotificationListenerService.updateSettings(telegramPackageName, postUrl, tokenValue, autoTrade);
            MainActivity.updateSettings(enableNotify);

            sendPostRequest(tokenValue);
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void sendPostRequest(String token) {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                String token = params[0];

                try {
                    URL url = new URL("https://capybara.s1.zetohosting.pl/settings.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    String postData = "token=" + token + "&autotrade=" + autoTrade;

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = postData.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i("req", "Server response: " + responseCode);
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
