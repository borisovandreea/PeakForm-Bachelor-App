package com.example.peakform.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.dao.ScoreSnapshotDao;
import com.example.peakform.data.entity.ScoreSnapshot;
import com.example.peakform.data.database.PeakFormDatabase;
import java.util.List;

public class ScoreRepository {
    private ScoreSnapshotDao scoreDao;

    public ScoreRepository(Application application) {
        scoreDao = PeakFormDatabase.getDatabase(application).scoreSnapshotDao();
    }

    public LiveData<List<ScoreSnapshot>> getScoreHistory() {
        return scoreDao.getScoreHistory();
    }


    public LiveData<ScoreSnapshot> getLatestScore() {
        return scoreDao.getLatestScore();
    }
}