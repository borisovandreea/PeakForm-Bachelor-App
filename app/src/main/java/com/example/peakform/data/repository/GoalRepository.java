package com.example.peakform.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.dao.GoalDao;
import com.example.peakform.data.entity.Goal;
import com.example.peakform.data.database.PeakFormDatabase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalRepository {
    private GoalDao goalDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public GoalRepository(Application application) {
        goalDao = PeakFormDatabase.getDatabase(application).goalDao();
    }

    //public LiveData<List<Goal>> getAllGoals() { return goalDao.getAllGoals(); }

    public void updateGoal(Goal goal) {
        executorService.execute(() -> goalDao.insert(goal)); // OnConflictStrategy.REPLACE handles update
    }
}