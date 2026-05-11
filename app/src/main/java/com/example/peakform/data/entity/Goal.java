package com.example.peakform.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
        @PrimaryKey(autoGenerate = true)
        public int id;

        public String domain;
        public float threshold;
        public String windowType;
}

