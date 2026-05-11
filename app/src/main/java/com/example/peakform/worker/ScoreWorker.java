package com.example.peakform.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.peakform.data.database.PeakFormDatabase;
import com.example.peakform.data.entity.Entry;
import com.example.peakform.data.entity.ScoreSnapshot;
import com.example.peakform.logic.scoring.ScoreCalculator;
import java.util.Calendar;
import java.util.List;

public class ScoreWorker extends Worker {

    public ScoreWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        PeakFormDatabase db = PeakFormDatabase.getDatabase(getApplicationContext());

        // 1. Calculăm intervalul pentru "astăzi" ca să luăm datele reale
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long start = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        long end = calendar.getTimeInMillis();

        // 2. Extragem toate activitățile logate de utilizator astăzi
        // Folosim varianta Sync (fără LiveData) pentru că Worker-ul rulează deja pe fundal
        List<Entry> todayEntries = db.entryDao().getEntriesForTodaySync(start, end);

        if (todayEntries == null || todayEntries.isEmpty()) {
            return Result.success(); // Nu avem ce calcula dacă nu s-a logat nimic
        }

        // 3. Calculăm scorul folosind noua metodă care folosește Focus, Impact și Life Balance
        int finalScore = ScoreCalculator.calculateDailyPerformance(todayEntries, getApplicationContext());

        // 4. Salvăm rezultatul în tabelul de ScoreSnapshot pentru grafic
        ScoreSnapshot snapshot = new ScoreSnapshot();
        snapshot.overallScore = (float) finalScore;
        snapshot.timestamp = System.currentTimeMillis();

        db.scoreSnapshotDao().insert(snapshot);

        return Result.success();
    }
}