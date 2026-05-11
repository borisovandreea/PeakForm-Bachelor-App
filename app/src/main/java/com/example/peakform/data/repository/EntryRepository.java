package com.example.peakform.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.peakform.data.database.PeakFormDatabase;
import com.example.peakform.data.dao.EntryDao;
import com.example.peakform.data.entity.Entry;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryRepository {
    private EntryDao entryDao;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public EntryRepository(Application application) {
        PeakFormDatabase db = PeakFormDatabase.getDatabase(application);
        entryDao = db.entryDao();
    }


    public LiveData<List<Entry>> getEntriesForToday() {
        long[] range = getDayRange(System.currentTimeMillis());
        return entryDao.getEntriesForToday(range[0], range[1]);
    }

    public float calculateScoreForToday() {
        return calculateScoreForDate(System.currentTimeMillis());
    }

    public float calculateScoreForDate(long timestamp) {
        long[] range = getDayRange(timestamp);

        List<Entry> entries = entryDao.getEntriesByDateSync(range[0], range[1]);

        if (entries == null || entries.isEmpty()) return 0f;

        int totalActual = 0;
        int totalGoal = 0;

        for (Entry e : entries) {
            totalActual += e.actualMinutes;
            totalGoal += e.goalMinutes;
        }

        if (totalGoal == 0) return 0f;
        return (float) totalActual / totalGoal;
    }

    public int getWeeklyTotalForDomain(String domain) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long startOfWeek = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        return entryDao.getSumMinutesForDomain(domain, startOfWeek, now);
    }



    public LiveData<List<Entry>> getAllEntries() {
        return entryDao.getAllEntries();
    }

    public void insert(Entry entry) {
        executorService.execute(() -> entryDao.insert(entry));
    }



    private long[] getDayRange(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long end = cal.getTimeInMillis();

        return new long[]{start, end};
    }

    public List<Entry> getAllEntriesSync() {
        return entryDao.getAllEntriesSync();
    }
}
