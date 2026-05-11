package com.example.peakform.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "feedback_events")
public class FeedbackEvent {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String message;
    public String bctType;
    public long timestamp;


    public String getDescription() {
        return message;
    }
}
