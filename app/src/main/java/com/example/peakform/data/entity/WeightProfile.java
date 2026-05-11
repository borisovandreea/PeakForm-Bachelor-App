package com.example.peakform.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weighting_profiles")
public class WeightProfile {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String profileName;
    public String domainWeightsJson;
    public boolean isActive;
}
