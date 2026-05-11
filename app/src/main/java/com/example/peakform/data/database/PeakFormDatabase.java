package com.example.peakform.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.peakform.data.dao.*;
import com.example.peakform.data.entity.*;

@Database(entities = {
        Domain.class,
        Entry.class,
        FeedbackEvent.class,
        Goal.class,
        ScoreSnapshot.class,
        WeightProfile.class
}, version = 1, exportSchema = false)
public abstract class PeakFormDatabase extends RoomDatabase {

    public abstract DomainDao domainDao();
    public abstract EntryDao entryDao();
    public abstract FeedbackEventDao feedbackEventDao();
    public abstract GoalDao goalDao();
    public abstract ScoreSnapshotDao scoreSnapshotDao();
    public abstract WeightProfileDao weightProfileDao();


    private static volatile PeakFormDatabase INSTANCE;

    public static PeakFormDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PeakFormDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PeakFormDatabase.class, "peakform_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}