package com.example.solexxsnifferv2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Arrays;

public class CustomFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);

        // Znalezienie pól edycyjnych, przycisku i Spinnera
        EditText asset = view.findViewById(R.id.asset);
        EditText entry = view.findViewById(R.id.entry);
        EditText tp1 = view.findViewById(R.id.tp1);
        EditText tp2 = view.findViewById(R.id.tp2);
        EditText tp3 = view.findViewById(R.id.tp3);
        EditText sl = view.findViewById(R.id.sl);
        Button submitButton = view.findViewById(R.id.submitButton);
        Spinner spinnerOptions = view.findViewById(R.id.spinnerOptions);

        // Ustawienie opcji dla Spinnera: "Buy" oraz "Sell"
        String[] options = {"buy", "sell"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        // Obsługa przycisku
        submitButton.setOnClickListener(v -> {
            String assetText = asset.getText().toString();
            String entryText = entry.getText().toString();
            String tp1Text = tp1.getText().toString();
            String tp2Text = tp2.getText().toString();
            String tp3Text = tp3.getText().toString();
            String slText = sl.getText().toString();
            String selectedOption = spinnerOptions.getSelectedItem().toString();

            // Sprawdzanie, czy pola są wypełnione
            if (!assetText.isEmpty() && !entryText.isEmpty() && !tp1Text.isEmpty() && !tp2Text.isEmpty() && !tp3Text.isEmpty() && !slText.isEmpty()) {
                String token = "KtE35AKrlEoUlo5xjWFPzWs0CYvWdhATqrakqlxj2Mbg9ZxRTFWlIHh1xTL5wBqf";
                sendDataToServer(token, assetText, entryText, new String[]{tp1Text, tp2Text, tp3Text}, new String[]{slText}, selectedOption);
            } else {
                Toast.makeText(getActivity(), "Wszystkie pola muszą być wypełnione!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void sendDataToServer(String token, String asset, String entry, String[] targetPrices, String[] stopLoss, String selectedOption) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Ustawienie URL
                    URL url = new URL("https://capybara.s1.zetohosting.pl/customSignal.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setDoOutput(true);

                    // Formatujemy tablice TP i SL jako ciągi w formacie ["value1","value2",...]
                    String takeProfit = "[" + Arrays.stream(targetPrices)
                            .map(s -> "\"" + s + "\"")
                            .collect(Collectors.joining(",")) + "]";
                    String stopLossStr = "[" + Arrays.stream(stopLoss)
                            .map(s -> "\"" + s + "\"")
                            .collect(Collectors.joining(",")) + "]";

                    // Tworzymy dane do przesłania jako klucz-wartość
                    String postData = "token=" + URLEncoder.encode(token, "UTF-8")
                            + "&asset=" + URLEncoder.encode(asset, "UTF-8")
                            + "&entry=" + URLEncoder.encode(entry, "UTF-8")
                            + "&takeProfit=" + URLEncoder.encode(takeProfit, "UTF-8")
                            + "&stopLoss=" + URLEncoder.encode(stopLossStr, "UTF-8")
                            + "&type=" + URLEncoder.encode(selectedOption, "UTF-8");

                    // Wysyłanie danych
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    // Sprawdzenie odpowiedzi serwera
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        return "Dane zostały pomyślnie przesłane";
                    } else {
                        return "Błąd: " + responseCode;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Błąd podczas przesyłania danych";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}