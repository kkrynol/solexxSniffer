package com.example.solexxsnifferv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private EditText textToken;
    private EditText textApp;
    private EditText textServer;
    private String tokenValue = "GPuO3vpOdl0Ox087qJulkrS0CvHpk0YEX7dzsSF028D2Rv92R41AJ6BKMwUc9y3l";
    private String telegramPackageName = "org.telegram.messenger";
    private String postUrl = "https://capybara.s1.zetohosting.pl/add.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textToken = view.findViewById(R.id.textToken);
        textApp = view.findViewById(R.id.textApp);
        textServer = view.findViewById(R.id.textServer);
        Button applyButton = view.findViewById(R.id.buttonApplySettings);

        // Obsługa kliknięcia przycisku
        applyButton.setOnClickListener(v -> {
            // Pobranie wartości z pól EditText
            String newToken = textToken.getText().toString().trim();
            String newAppPackage = textApp.getText().toString().trim();
            String newServerUrl = textServer.getText().toString().trim();

            // Zaktualizowanie zmiennych statycznych
            tokenValue = newToken.isEmpty() ? tokenValue : newToken;
            telegramPackageName = newAppPackage.isEmpty() ? telegramPackageName : newAppPackage;
            postUrl = newServerUrl.isEmpty() ? postUrl : newServerUrl;

            // Opcjonalnie: Możesz przekazać te wartości do swojej klasy `CustomNotificationListenerService`
            CustomNotificationListenerService.updateSettings(telegramPackageName, postUrl, tokenValue);
        });

        return view;
    }
}
