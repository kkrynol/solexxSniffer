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
    private List<NotificationData> notifications = new ArrayList<>();
    private Handler handler = new Handler();
    private DataListFragment dataListFragment;
    private int currentListSize = 0;

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
                        return new CustomFragment();
                    default:
                        throw new IllegalStateException("Unexpected position: " + position);
                }
            }

            @Override
            public int getItemCount() {
                return 3;
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

        InitData.sendGetRequest(Common.GetToken());

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
                    String url = Common.GetServerUrl();
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    urlConnection.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder jsonResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResponse.append(line);
                    }
                    reader.close();

                    String response = jsonResponse.toString();

                    if (response.contains("\"message\":\"no data\"")) {
                        return new ArrayList<>();
                    }

                    Gson gson = new Gson();
                    return gson.fromJson(response, new TypeToken<List<NotificationData>>() {}.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(List<NotificationData> data) {
                if (data != null && !data.isEmpty()) {
                    Collections.reverse(data);
                    notifications = data;

                    if (dataListFragment != null) {
                        dataListFragment.updateData(notifications);
                    }

                    if(data.size() == 0) {
                        currentListSize = 0;
                    }
                    if (data.size() > currentListSize) {
                        currentListSize = data.size();
                        showNotification("New Post!", "New Post!");
                    }
                } else {
                    if (dataListFragment != null) {
                        dataListFragment.updateData(new ArrayList<>());
                    }

                    if (data == null) {
                        Toast.makeText(MainActivity.this, "Download data error", Toast.LENGTH_SHORT).show();
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
