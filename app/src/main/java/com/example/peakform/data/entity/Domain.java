package com.example.peakform.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "domains")
public class Domain {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String iconName;
    public String colorHex;
}
