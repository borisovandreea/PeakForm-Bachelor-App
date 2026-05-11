package com.example.peakform.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "score_snapshots")
public class ScoreSnapshot {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public float overallScore;
    public long timestamp;
}
