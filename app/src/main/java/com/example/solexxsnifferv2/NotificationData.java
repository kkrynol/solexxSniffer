package com.example.solexxsnifferv2;

public class NotificationData {
    private String title;
    private String message;

    // Konstruktor
    public NotificationData(String title, String message) {
        this.title = title;
        this.message = message;
    }

    // Getter dla tytułu
    public String getTitle() {
        return title;
    }

    // Getter dla wiadomości
    public String getMessage() {
        return message;
    }

    // Setter dla tytułu
    public void setTitle(String title) {
        this.title = title;
    }

    // Setter dla wiadomości
    public void setMessage(String message) {
        this.message = message;
    }
}
