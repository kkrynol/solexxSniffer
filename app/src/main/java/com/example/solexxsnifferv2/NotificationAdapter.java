package com.example.solexxsnifferv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationData> notifications;

    public NotificationAdapter(List<NotificationData> notifications) {
        this.notifications = notifications;
    }

    public void updateData(List<NotificationData> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();  // Powiadomienie adaptera, aby odświeżył dane
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationData notification = notifications.get(position);
        // Ustaw dane w widoku
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getText());
        holder.dateTextView.setText(notification.getDate()); // Ustaw datę
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        TextView dateTextView; // Dodane pole dla daty

        public NotificationViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView); // Inicjalizacja widoku daty
        }
    }
}
