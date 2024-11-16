package com.example.solexxsnifferv2;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CustomNotificationListenerService extends NotificationListenerService {
    private static String TAG = "NotificationListener";
    private static String PACKAGE_NAME = "org.telegram.messenger";
    //private static String PACKAGE_NAME = "com.example.solexx";

    private static String POST_URL = "https://capybara.s1.zetohosting.pl/add.php";
    private static String TOKEN = "KtE35AKrlEoUlo5xjWFPzWs0CYvWdhATqrakqlxj2Mbg9ZxRTFWlIHh1xTL5wBqf";
    private static int AUTOTRADE = 0;

    public static void updateSettings(String packageName, String postUrl, String token, int autoTrade) {
        PACKAGE_NAME = packageName;
        POST_URL = postUrl + "/add.php";
        TOKEN = token;
        AUTOTRADE = autoTrade;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (PACKAGE_NAME.equals(sbn.getPackageName())) {
            Notification notification = sbn.getNotification();
            if (notification != null) {
                Bundle extras = notification.extras;
                String title = extras.getString(Notification.EXTRA_TITLE);
                String text = extras.getCharSequence(Notification.EXTRA_TEXT) != null
                        ? extras.getCharSequence(Notification.EXTRA_TEXT).toString()
                        : "No data";
                sendPostRequest(title, text, TOKEN, AUTOTRADE);
            }
        }
    }

    private void sendPostRequest(String title, String text, String token, int autoTrade) {
        new Thread(() -> {
            try {
                URL url = new URL(POST_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);

                // Tworzenie payloadu do wysłania
                String postData = "title=" + title + "&text=" + text + "&token=" + token + "&autotrade=" + autoTrade;
                byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

                OutputStream os = urlConnection.getOutputStream();
                os.write(postDataBytes);
                os.flush();
                os.close();

                // Odbieranie odpowiedzi
                int responseCode = urlConnection.getResponseCode();
                Log.i(TAG, "Server response: " + responseCode);
                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "ERROR: ", e);
            }
        }).start();
    }
}
