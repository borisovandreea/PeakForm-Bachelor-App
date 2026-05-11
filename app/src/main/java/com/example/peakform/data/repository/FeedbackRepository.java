package com.example.peakform.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.database.PeakFormDatabase;
import com.example.peakform.data.dao.FeedbackEventDao;
import com.example.peakform.data.entity.FeedbackEvent;
import com.example.peakform.data.repository.EntryRepository; // Added
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedbackRepository {
    private FeedbackEventDao feedbackDao;
    private EntryRepository entryRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FeedbackRepository(Application application) {
        feedbackDao = PeakFormDatabase.getDatabase(application).feedbackEventDao();
        entryRepository = new EntryRepository(application);
    }

    public LiveData<List<FeedbackEvent>> getFeedbackHistory() {
        return feedbackDao.getFeedbackHistory();
    }

    public LiveData<FeedbackEvent> getLatestFeedback() {
        return feedbackDao.getLatestFeedback();
    }

    public void logFeedback(FeedbackEvent event) {
        executorService.execute(() -> feedbackDao.insert(event));
    }

    public void generateDailySnapshot(long timestamp, String dateLabel) {
        executorService.execute(() -> {
            float performanceScore = entryRepository.calculateScoreForDate(timestamp);

            String message = generateRecommendation(performanceScore);
            String type = (performanceScore >= 80) ? "achievement" : "suggestion";


            FeedbackEvent snapshot = new FeedbackEvent();
            snapshot.message = message;
            snapshot.bctType = type;
            snapshot.timestamp = timestamp;

            feedbackDao.insert(snapshot);
        });
    }

    private String generateRecommendation(float score) {
        if (score >= 90) return "Exceptional! You hit almost all your minute targets. Consistency is your superpower.";
        if (score >= 70) return "Strong day. You're following your performance settings well. Keep this momentum!";
        if (score >= 40) return "A balanced effort. To reach Peak Form, try to focus more on your high-impact domains tomorrow.";
        return "A rest day? That's okay. Check your 'Adjust Goals' button if the minute targets feel too high.";
    }
}
