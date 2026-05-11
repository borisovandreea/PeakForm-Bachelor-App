package com.example.peakform.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.peakform.data.entity.Entry;
import com.example.peakform.data.entity.FeedbackEvent;
import com.example.peakform.data.repository.EntryRepository;
import com.example.peakform.data.repository.FeedbackRepository;
import com.example.peakform.logic.settings.SettingsManager;

import java.util.List;

public class FeedbackViewModel extends AndroidViewModel {
    private FeedbackRepository feedbackRepository;
    private EntryRepository entryRepository;
    private SettingsManager settingsManager;

    private LiveData<List<FeedbackEvent>> feedbackHistory;
    private LiveData<FeedbackEvent> latestInsight;

    public FeedbackViewModel(@NonNull Application application) {
        super(application);
        feedbackRepository = new FeedbackRepository(application);
        entryRepository = new EntryRepository(application);
        settingsManager = new SettingsManager(application);

        feedbackHistory = feedbackRepository.getFeedbackHistory();
        latestInsight = feedbackRepository.getLatestFeedback();
    }


    public LiveData<List<FeedbackEvent>> getFeedbackHistory() {
        return feedbackHistory;
    }

    public LiveData<FeedbackEvent> getLatestInsight() {
        return latestInsight;
    }

    public float getTodayPerformanceScore() {
        return entryRepository.calculateScoreForToday();
    }

    public List<String> getAllDomains() {
        return settingsManager.getAllDomains();
    }

    public int getWeeklyGoalForDomain(String domain) {
        return settingsManager.getWeeklyMinutesTarget(domain);
    }

    public int getWeeklyActualForDomain(String domain) {
        return entryRepository.getWeeklyTotalForDomain(domain);
    }
}