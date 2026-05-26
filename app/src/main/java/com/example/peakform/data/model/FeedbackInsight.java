package com.example.peakform.data.model;

/**
 * This is a UI Model. It exists only to tell the Adapter
 * what text and what background color to show in the list.
 */
public class FeedbackInsight {
    public long timestamp;
    public String message;
    public int colorResId; // The ID of the pastel color (Green, Blue, or Purple)

    public FeedbackInsight(long timestamp, String message, int colorResId) {
        this.timestamp = timestamp;
        this.message = message;
        this.colorResId = colorResId;
    }
}
