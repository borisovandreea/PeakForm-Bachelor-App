package com.example.peakform.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.entity.WeightProfile;
import com.example.peakform.data.repository.WeightProfileRepository;
import com.example.peakform.data.database.PeakFormDatabase;

public class SettingsViewModel extends AndroidViewModel {
    private WeightProfileRepository weightRepository;
    private LiveData<WeightProfile> activeProfile;

    public SettingsViewModel(Application application) {
        super(application);
        weightRepository = new WeightProfileRepository(application);
        activeProfile = weightRepository.getActiveProfile();
    }

    public LiveData<WeightProfile> getActiveProfile() { return activeProfile; }

    public void saveWeightProfile(WeightProfile profile) {
        weightRepository.saveProfile(profile);
    }


    public void deleteUserActivity() {
        new Thread(() -> {
            PeakFormDatabase db = PeakFormDatabase.getDatabase(getApplication());
            db.entryDao().deleteAllEntries();
            db.scoreSnapshotDao().deleteAllScores();
            db.feedbackEventDao().deleteAllFeedback();
        }).start();
    }

    public void deleteFullProfile(Context context) {
        new Thread(() -> {

            PeakFormDatabase.getDatabase(getApplication()).clearAllTables();


            SharedPreferences prefs = context.getSharedPreferences("PeakFormPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();


        }).start();
    }
}
