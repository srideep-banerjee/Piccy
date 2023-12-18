package com.example.piccy.model;

public abstract class StatusUpdateListener {
    private String currentStatus = "";

    public abstract void onStatusUpdate(String newStatus);

    public void updateStatus(String status) {
        if (!status.equals(currentStatus)) {
            currentStatus = status;
            onStatusUpdate(status);
        }
    }
}
