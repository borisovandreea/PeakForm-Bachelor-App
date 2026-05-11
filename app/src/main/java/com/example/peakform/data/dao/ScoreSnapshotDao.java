package com.example.peakform.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.peakform.data.entity.ScoreSnapshot;

import java.util.List;

@Dao
public interface ScoreSnapshotDao {
    @Insert
    long insert(ScoreSnapshot snapshot);

    @Query("SELECT * FROM score_snapshots ORDER BY timestamp DESC LIMIT 1")
    LiveData<ScoreSnapshot> getLatestScore();


    @Query("SELECT * FROM score_snapshots ORDER BY timestamp ASC")
    LiveData<List<ScoreSnapshot>> getScoreHistory();

    @Query("DELETE FROM score_snapshots")
    void deleteAllScores();
}
