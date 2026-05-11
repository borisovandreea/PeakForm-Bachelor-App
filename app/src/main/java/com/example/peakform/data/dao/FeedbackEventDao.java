package com.example.peakform.data.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.peakform.data.entity.FeedbackEvent;

import java.util.List;

@Dao
public interface FeedbackEventDao {
    @Insert
    long insert(FeedbackEvent feedbackEvent);
    @Query("SELECT * FROM feedback_events ORDER BY timestamp DESC")
    LiveData<List<FeedbackEvent>> getFeedbackHistory();
    @Query("SELECT * FROM feedback_events ORDER BY timestamp DESC LIMIT 1")
    LiveData<FeedbackEvent> getLatestFeedback();

    @Query("DELETE FROM feedback_events")
    void deleteAllFeedback();
}