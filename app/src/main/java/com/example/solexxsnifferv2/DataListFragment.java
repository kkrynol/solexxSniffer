package com.example.solexxsnifferv2;

import android.os.Bundle;
import android.util.Log; // Dodajemy logowanie
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataListFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data_list, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicjalizuj adapter
        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    // Metoda do zaktualizowania danych w adapterze
    public void updateData(List<NotificationData> notifications) {
        if (adapter != null) {
            Log.d("DataListFragment", "Aktualizacja danych: " + notifications.toString());
            adapter.updateData(notifications);  // Przekazywanie nowych danych do adaptera
        } else {
            Log.e("DataListFragment", "Adapter jest nullem, nie można zaktualizować danych.");
        }
    }
}
