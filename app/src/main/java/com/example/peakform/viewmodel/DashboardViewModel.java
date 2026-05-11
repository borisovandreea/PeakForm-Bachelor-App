package com.example.peakform.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.entity.ScoreSnapshot;
import com.example.peakform.data.repository.ScoreRepository;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private ScoreRepository repository;
    private LiveData<ScoreSnapshot> latestScore;
    private LiveData<List<ScoreSnapshot>> scoreHistory;

    public DashboardViewModel(Application application) {
        super(application);
        repository = new ScoreRepository(application);
        latestScore = repository.getLatestScore();
        scoreHistory = repository.getScoreHistory();
    }

    public LiveData<ScoreSnapshot> getLatestScore() { return latestScore; }
    public LiveData<List<ScoreSnapshot>> getScoreHistory() { return scoreHistory; }
}
