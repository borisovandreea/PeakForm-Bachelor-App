package com.example.peakform.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.dao.WeightProfileDao;
import com.example.peakform.data.entity.WeightProfile;
import com.example.peakform.data.database.PeakFormDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeightProfileRepository {
    private WeightProfileDao weightDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WeightProfileRepository(Application application) {
        weightDao = PeakFormDatabase.getDatabase(application).weightProfileDao();
    }

    public LiveData<WeightProfile> getActiveProfile() {
        return weightDao.getActiveProfile();
    }

    public void saveProfile(WeightProfile profile) {
        executorService.execute(() -> weightDao.insert(profile));
    }
}
