package com.example.peakform.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entries")
public class Entry {
    @PrimaryKey(autoGenerate = true)
    public int id;


    public String activityName;
    public String domainName;
    public int goalMinutes;
    public int actualMinutes;
    public int focusScore;
    public int impactScore;

    public long timestamp;
}