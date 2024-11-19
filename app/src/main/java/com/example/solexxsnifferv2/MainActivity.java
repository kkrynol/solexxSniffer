package com.example.solexxsnifferv2;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_URL = "https://capybara.s1.zetohosting.pl/";
    private NotificationAdapter adapter;
    private List<NotificationData> notifications = new ArrayList<>();
    private Handler handler = new Handler();
    private DataListFragment dataListFragment;
    private int currentListSize = 0; // Dodane pole do przechowywania aktualnej liczby elementów w liście UI

    private static boolean enableNotify = false;
    public static void updateSettings(boolean flag)
    {
        enableNotify = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        dataListFragment = new DataListFragment();
                        return dataListFragment;
                    case 1:
                        return new SettingsFragment();
                    case 2:
                        return new CustomFragment(); // Dodajemy nowy fragment
                    default:
                        throw new IllegalStateException("Unexpected position: " + position);
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Zwiększ liczbę zakładek
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Posts");
                    break;
                case 1:
                    tab.setText("Settings");
                    break;
                case 2:
                    tab.setText("Custom");
                    break;
            }
        }).attach();

        String token = "KtE35AKrlEoUlo5xjWFPzWs0CYvWdhATqrakqlxj2Mbg9ZxRTFWlIHh1xTL5wBqf";
        InitData.sendGetRequest(token);

        // Odświeżanie danych co 2 sekundy
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchDataFromServer();
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchDataFromServer() {
        new AsyncTask<Void, Void, List<NotificationData>>() {

            @Override
            protected List<NotificationData> doInBackground(Void... voids) {
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(DATA_URL).openConnection();
                    urlConnection.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder jsonResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResponse.append(line);
                    }
                    reader.close();

                    String response = jsonResponse.toString();

                    // Sprawdzanie, czy odpowiedź zawiera "message": "no data"
                    if (response.contains("\"message\":\"no data\"")) {
                        return new ArrayList<>(); // Zwracamy pustą listę, gdy brak danych
                    }

                    // Przetwarzanie danych, jeśli odpowiedź jest prawidłowa
                    Gson gson = new Gson();
                    return gson.fromJson(response, new TypeToken<List<NotificationData>>() {}.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null; // Zwracamy null w przypadku błędu
                }
            }

            @Override
            protected void onPostExecute(List<NotificationData> data) {
                if (data != null && !data.isEmpty()) {
                    Log.i("test", "dane otrzymane");

                    Collections.reverse(data);
                    notifications = data;

                    // Zaktualizowanie widoku w fragmencie
                    if (dataListFragment != null) {
                        dataListFragment.updateData(notifications);
                    }

                    // Sprawdzenie, czy nowa lista ma więcej elementów niż aktualna lista w UI
                    if(data.size() == 0)
                    {
                        currentListSize = 0;
                    }
                    if (data.size() > currentListSize) {
                        currentListSize = data.size(); // Zaktualizuj rozmiar listy
                        showNotification("Nowy element w Signals", "Pojawił się nowy element!");
                    }
                } else {
                    // Jeśli brak danych lub wystąpił błąd, wyczyszczamy listę
                    if (dataListFragment != null) {
                        dataListFragment.updateData(new ArrayList<>()); // Wyczyść dane w UI
                    }

                    // Jeżeli dane są null, pokazujemy komunikat o błędzie
                    if (data == null) {
                        Toast.makeText(MainActivity.this, "Błąd podczas pobierania danych", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute();
    }

    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "signals_channel";
            CharSequence channelName = "Signals Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new Notification.Builder(this, "signals_channel")
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        notificationManager.notify(0, notification);
    }
}
